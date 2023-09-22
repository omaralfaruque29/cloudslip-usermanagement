package com.cloudslip.usermanagement.helper.Git;

import com.cloudslip.usermanagement.core.CustomRestTemplate;
import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.Company;
import com.cloudslip.usermanagement.repository.CompanyRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GetGitBranchesHelper extends AbstractHelper{

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CustomRestTemplate restTemplate;

    private final Logger log = LoggerFactory.getLogger(GetGitBranchesHelper.class);
    private ResponseDTO output = new ResponseDTO();
    private Company company;
    private List<String> branches;
    private String repo;

    protected void init(BaseInput input, Object... extraParams) {
        this.repo = extraParams[0].toString();
        this.setOutput(output);
    }

    protected void checkPermission() {
        if (requester == null || requester.hasAuthority(Authority.ANONYMOUS) || requester.hasAuthority(Authority.ROLE_AGENT_SERVICE) || requester.hasAuthority(Authority.ROLE_GIT_AGENT)) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void checkValidity() {
        Optional<Company> companyOptional= Optional.of(companyRepository.findById(new ObjectId(requester.getCompanyIdAsString())).get());
        company = companyOptional.get();
        if(company == null){
            output.generateErrorResponse("No company of this user");
            throw new ApiErrorException(this.getClass().getName());
        } else if (company.getGitInfo() == null){
            output.generateErrorResponse("No git info of this user's company, update general settings");
            throw new ApiErrorException(this.getClass().getName());
        } else if (company.getGitInfo().getUsername() == null){
            output.generateErrorResponse("Git ID missing of this user's company, update general settings");
            throw new ApiErrorException(this.getClass().getName());
        }
    }

    protected void doPerform() {
        if(company.getGitInfo().getGitProvider().equalsIgnoreCase("GITHUB")){
            fetchGithubRepoBranches();
        } else if (company.getGitInfo().getGitProvider().equalsIgnoreCase("BITBUCKET")){
            fetchBitbucketRepoBranches();
        } else if (company.getGitInfo().getGitProvider().equalsIgnoreCase("GITLAB")){
            fetchGitlabRepoBranches();
        }
    }

    protected void postPerformCheck() {
    }

    protected void doRollback() {
    }

    protected void fetchGithubRepoBranches(){
        try {
            branches = new ArrayList<>();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("Authorization", "token " + company.getGitInfo().getSecretKey());
            HttpEntity<String> request = new HttpEntity<>(headers);
            String gitUrl = "https://api.github.com/repos/" + company.getGitInfo().getUsername() + "/" + repo + "/branches";
            ResponseEntity<Object> gitResponse = restTemplate.exchange(gitUrl, HttpMethod.GET, request, Object.class);
            ArrayList<LinkedHashMap> branchList = (ArrayList<LinkedHashMap>)gitResponse.getBody();
            System.out.println(branchList);
            for (LinkedHashMap branch : branchList) {
                branches.add((String) branch.get("name"));
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            output.generateErrorResponse("Failed to retrieve branches of the repository");
            throw new ApiErrorException(this.getClass().getName());
        }
        output.generateSuccessResponse(branches, "git branches retrieved");
    }


    protected void fetchBitbucketRepoBranches(){
        try {
            branches = new ArrayList<>();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.ALL.APPLICATION_JSON));
            headers.add("Authorization","Bearer " + company.getGitInfo().getSecretKey());
            HttpEntity<String> request = new HttpEntity<>(headers);
            String gitUrl = "https://api.bitbucket.org/2.0/repositories/" + company.getGitInfo().getUsername() + "/" + repo + "/refs/branches";
            ResponseEntity<Object> gitResponse = restTemplate.exchange(gitUrl, HttpMethod.GET, request, Object.class);
            LinkedHashMap map = (LinkedHashMap) gitResponse.getBody();
            ArrayList list = (ArrayList) map.get("values");
            for (Object object : list){
                LinkedHashMap linkedHashMap = (LinkedHashMap) object;
                branches.add((String) linkedHashMap.get("name"));
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            output.generateErrorResponse("Failed to retrieve branches of the repository");
            throw new ApiErrorException(this.getClass().getName());
        }
        output.generateSuccessResponse(branches, "git branches retrieved");
    }

    protected void fetchGitlabRepoBranches(){
        try {
            branches = new ArrayList<>();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.ALL.APPLICATION_JSON));
            headers.add("Authorization","Bearer " + company.getGitInfo().getSecretKey());
            HttpEntity<String> request = new HttpEntity<>(headers);

            String gitUrlForProject = "https://gitlab.com/api/v4/projects?search=" + repo;
            ResponseEntity<Object> gitResponseForProject = restTemplate.exchange(gitUrlForProject, HttpMethod.GET, request, Object.class);
            ArrayList arrayList = (ArrayList) gitResponseForProject.getBody();
            LinkedHashMap linkedHashMap = (LinkedHashMap) arrayList.get(0);
            int projectId = (int) linkedHashMap.get("id");

            String gitUrlForBranches = "https://gitlab.com/api/v4/projects/" + projectId + "/protected_branches";
            ResponseEntity<Object> gitResponseForBranches = restTemplate.exchange(gitUrlForBranches, HttpMethod.GET, request, Object.class);
            ArrayList list = (ArrayList) gitResponseForBranches.getBody();
            for(int i=0; i<list.size(); i++){
                LinkedHashMap linkedHashMapBranch = (LinkedHashMap) list.get(i);
                branches.add((String) linkedHashMapBranch.get("name"));
            }

        } catch (Exception ex) {
            log.error(ex.getMessage());
            output.generateErrorResponse("Failed to retrieve branches of the repository");
            throw new ApiErrorException(this.getClass().getName());
        }
        output.generateSuccessResponse(branches, "git branches retrieved");
    }

}
