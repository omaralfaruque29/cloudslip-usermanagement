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
public class GetGitRepositoriesHelper extends AbstractHelper{

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CustomRestTemplate restTemplate;

    private final Logger log = LoggerFactory.getLogger(GetGitRepositoriesHelper.class);
    private ResponseDTO output = new ResponseDTO();
    private List<String> repositories;
    private Company company;

    protected void init(BaseInput input, Object... extraParams) {
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

    protected void doPerform(){
        if(company.getGitInfo().getGitProvider().equalsIgnoreCase("GITHUB")){
            fetchGithubRepos();
        } else if (company.getGitInfo().getGitProvider().equalsIgnoreCase("BITBUCKET")){
            fetchBitbucketRepos();
        } else if (company.getGitInfo().getGitProvider().equalsIgnoreCase("GITLAB")){
            fetchGitlabRepos();
        }
    }

    protected void postPerformCheck() {
    }

    protected void doRollback() {
    }

    protected void fetchGithubRepos(){
        try{
            repositories = new ArrayList<>();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.ALL.APPLICATION_JSON));
            headers.add("Authorization","token " + company.getGitInfo().getSecretKey());
            HttpEntity<String> request = new HttpEntity<>(headers);
            String gitUrl = "https://api.github.com/user/repos";
            ResponseEntity<Object> gitResponse = restTemplate.exchange(gitUrl, HttpMethod.GET, request, Object.class);
            ArrayList<LinkedHashMap> repoList = (ArrayList<LinkedHashMap>)gitResponse.getBody();
            for (LinkedHashMap repo : repoList) {
                repositories.add((String) repo.get("name"));
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            output.generateErrorResponse("Failed to retrieve repositories");
            throw new ApiErrorException(this.getClass().getName());
        }
        output.generateSuccessResponse(repositories, "repositories retrieved");
    }

    protected void fetchBitbucketRepos(){
        try{
            repositories = new ArrayList<>();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.ALL.APPLICATION_JSON));
            headers.add("Authorization","Bearer " + company.getGitInfo().getSecretKey());
            HttpEntity<String> request = new HttpEntity<>(headers);
            String gitUrl = "https://api.bitbucket.org/2.0/repositories/" + company.getGitInfo().getUsername();
            ResponseEntity<Object> gitResponse = restTemplate.exchange(gitUrl, HttpMethod.GET, request, Object.class);
            LinkedHashMap linkedHashMap = (LinkedHashMap) gitResponse.getBody();
            ArrayList arrayList = (ArrayList) linkedHashMap.get("values");
            for(int i=0; i<arrayList.size(); i++){
                LinkedHashMap linkedHashMap1 = (LinkedHashMap) arrayList.get(i);
                repositories.add((String) linkedHashMap1.get("name"));
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            output.generateErrorResponse("Failed to retrieve repositories");
            throw new ApiErrorException(this.getClass().getName());
        }
        output.generateSuccessResponse(repositories, "repositories retrieved");
    }

    protected void fetchGitlabRepos(){
        try{
            repositories = new ArrayList<>();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.ALL.APPLICATION_JSON));
            headers.add("Authorization","Bearer " + company.getGitInfo().getSecretKey());
            HttpEntity<String> request = new HttpEntity<>(headers);
            String gitUrl = "https://gitlab.com/api/v4/users/" + company.getGitInfo().getUsername() + "/projects";
            ResponseEntity<Object> gitResponse = restTemplate.exchange(gitUrl, HttpMethod.GET, request, Object.class);
            ArrayList arrayList = (ArrayList) gitResponse.getBody();
            for(int i=0; i<arrayList.size(); i++){
                LinkedHashMap linkedHashMap = (LinkedHashMap) arrayList.get(i);
                repositories.add((String) linkedHashMap.get("name"));
            }

        } catch (Exception ex) {
            log.error(ex.getMessage());
            output.generateErrorResponse("Failed to retrieve repositories");
            throw new ApiErrorException(this.getClass().getName());
        }
        output.generateSuccessResponse(repositories, "repositories retrieved");
    }

}
