package com.cloudslip.usermanagement.helper.organization;

import com.cloudslip.usermanagement.core.CustomRestTemplate;
import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.Company;
import com.cloudslip.usermanagement.model.GitInfo;
import com.cloudslip.usermanagement.model.UserInfo;
import com.cloudslip.usermanagement.model.dummy.GitDirectory;
import com.cloudslip.usermanagement.model.dummy.GithubOrganization;
import com.cloudslip.usermanagement.repository.CompanyRepository;
import com.cloudslip.usermanagement.repository.UserInfoRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;


@Service
public class GetGitDirectoryListHelper extends AbstractHelper {
    private final Logger log = LoggerFactory.getLogger(GetGitDirectoryListHelper.class);

    private ResponseDTO output = new ResponseDTO();
    private UserInfo requesterInfo;

    @Autowired
    private CompanyRepository companyRepository;
    
    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomRestTemplate restTemplate;

    protected void init(BaseInput input, Object... extraParams) {
        this.setOutput(output);
    }

    protected void checkPermission() {
        if (requester == null || (!requester.hasAuthority(Authority.ROLE_SUPER_ADMIN) && !requester.hasAuthority(Authority.ROLE_ADMIN))) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }

    protected void checkValidity() {
        requesterInfo = userInfoRepository.findByUserId(requester.getObjectId()).get();
        if(requesterInfo.getCompany() == null) {
            output.generateErrorResponse("Requester doesn't belong to any Company!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void doPerform() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        GitInfo companyGitInfo = getCompanyGitInfo();
        if(companyGitInfo == null) {
            output.generateErrorResponse("Company Git Information not found!");
        } else if (companyGitInfo.getGitProvider().equals("GITHUB")){
            fetchGitOrganizations(companyGitInfo);
        } else if (companyGitInfo.getGitProvider().equals("BITBUCKET")){
            fetchBitbucketTeams(companyGitInfo);
        } else if (companyGitInfo.getGitProvider().equals("GITLAB")){
            fetchGitlabGroups(companyGitInfo);
        }
    }

    private void fetchGitOrganizations(GitInfo companyGitInfo) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Authorization","Token " + companyGitInfo.getSecretKey());
        HttpEntity<String> request = new HttpEntity<>(headers);
        final String gitUrl = "https://api.github.com/user/orgs";
        RestTemplate restTemplate = new RestTemplate();
        Object object = restTemplate.exchange(gitUrl, HttpMethod.GET, request, Object.class);
        Class clazz = object.getClass();
        Method method = clazz.getMethod("getBody");
        Object body = method.invoke(object);
        List<GithubOrganization> githubOrganizationList = objectMapper.convertValue(body, new TypeReference<List<GithubOrganization>>() { });

        List<GitDirectory> gitDirectoryList = new ArrayList<>();

        for(GithubOrganization githubOrganization : githubOrganizationList) {
            GitDirectory gitDirectory = new GitDirectory(githubOrganization);
            gitDirectoryList.add(gitDirectory);
        }

        output.generateSuccessResponse(gitDirectoryList);
    }

    private void fetchBitbucketTeams(GitInfo companyGitInfo) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ArrayList<String> teams = new ArrayList<>();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.ALL.APPLICATION_JSON));
            headers.add("Authorization","Bearer " + companyGitInfo.getSecretKey());
            HttpEntity<String> request = new HttpEntity<>(headers);
            final String gitUrl = "https://api.bitbucket.org/2.0/teams?role=admin";
            ResponseEntity<Object> gitResponse = restTemplate.exchange(gitUrl, HttpMethod.GET, request, Object.class);
            LinkedHashMap linkedHashMap = (LinkedHashMap) gitResponse.getBody();
            ArrayList arrayList = (ArrayList) linkedHashMap.get("values");
            for(int i=0; i<arrayList.size(); i++){
                LinkedHashMap linkedHashMap1 = (LinkedHashMap) arrayList.get(i);
                teams.add((String) linkedHashMap1.get("username"));
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            output.generateErrorResponse("Failed to retrieve bitbucket teams");
            throw new ApiErrorException(this.getClass().getName());
        }
        output.generateSuccessResponse(teams, "teams retrieved");
    }

    private void fetchGitlabGroups(GitInfo companyGitInfo) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ArrayList<String> groups = new ArrayList<>();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.ALL.APPLICATION_JSON));
            headers.add("Authorization","Bearer " + companyGitInfo.getSecretKey());
            HttpEntity<String> request = new HttpEntity<>(headers);
            final String gitUrl = "https://gitlab.com/api/v4/groups";
            ResponseEntity<Object> gitResponse = restTemplate.exchange(gitUrl, HttpMethod.GET, request, Object.class);
            ArrayList<LinkedHashMap> arrayList = (ArrayList) gitResponse.getBody();
            for(int i=0; i<arrayList.size(); i++){
                LinkedHashMap linkedHashMap = (LinkedHashMap) arrayList.get(i);
                groups.add((String) linkedHashMap.get("name"));
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            output.generateErrorResponse("Failed to retrieve gitlab groups");
            throw new ApiErrorException(this.getClass().getName());
        }
        output.generateSuccessResponse(groups, "gitlab groups retrieved");
    }

    private GitInfo getCompanyGitInfo() {
        GitInfo gitInfo = null;
        if(requesterInfo.getCompany().getGitInfo() == null) {
            Company requesterCompany = companyRepository.findById(requesterInfo.getCompany().getObjectId()).get();
            gitInfo = requesterCompany.getGitInfo();
        } else {
            gitInfo = requesterInfo.getCompany().getGitInfo();
        }
        return gitInfo;
    }

    protected void postPerformCheck() {

    }

    protected void doRollback() {

    }
}
