package com.cloudslip.usermanagement.helper.company;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.dto.SaveCompanyGitInfoDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.Company;
import com.cloudslip.usermanagement.model.GitInfo;
import com.cloudslip.usermanagement.model.UserInfo;
import com.cloudslip.usermanagement.model.dummy.DockerHubInfo;
import com.cloudslip.usermanagement.repository.CompanyRepository;
import com.cloudslip.usermanagement.repository.UserInfoRepository;
import com.cloudslip.usermanagement.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class SaveCompanyGitInfoHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(SaveCompanyGitInfoHelper.class);

    private SaveCompanyGitInfoDTO input;
    private ResponseDTO output = new ResponseDTO();
    private Company updatingCompany;


    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;


    public void init(BaseInput input, Object... extraParams) {
        this.input = (SaveCompanyGitInfoDTO) input;
        this.setOutput(output);
    }


    protected void checkPermission() {
        if (requester == null || (!requester.hasAuthority(Authority.ROLE_SUPER_ADMIN) && !requester.hasAuthority(Authority.ROLE_ADMIN))) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void checkValidity() {
        this.validateRequiredFields();
        Optional<Company> company = null;
        UserInfo currentUserInfo = userInfoRepository.findByUserId(requester.getObjectId()).get();
        if(requester.hasAuthority(Authority.ROLE_SUPER_ADMIN) && input.getCompanyId() != null) {
            company = companyRepository.findById(input.getCompanyId());
        } else if(requester.hasAuthority(Authority.ROLE_ADMIN)) {
            company = companyRepository.findById(currentUserInfo.getCompany().getObjectId());
        } else {
            output.generateErrorResponse("Invalid post data!");
            throw new ApiErrorException(this.getClass().getName());
        }
        if(!(company.isPresent())) {
            output.generateErrorResponse(String.format("No Company found with the id - %s", currentUserInfo.getCompany().getId()));
            throw new ApiErrorException(this.getClass().getName());
        }
        updatingCompany = company.get();
    }

    protected void doPerform() {
        GitInfo gitInfo = new GitInfo(input.getGitProvider(), input.getUsername(), input.getApiUrl(), input.getSecretKey());
        updatingCompany.setGitInfo(gitInfo);
        updatingCompany.setUpdatedBy(requester.getUsername());
        updatingCompany.setUpdateDate(String.valueOf(LocalDateTime.now()));
        updatingCompany.setLastUpdateActionId(actionId);
        companyRepository.save(updatingCompany);
        output.generateSuccessResponse(null, String.format("Git information is updated", updatingCompany.getName()));
    }


    protected void postPerformCheck() {
    }

    protected void doRollback() {

    }

    private void validateRequiredFields() {
        if (input.getGitProvider() == null || input.getGitProvider().equals("")) {
            output.generateErrorResponse("Github provider is required!");
            throw new ApiErrorException(this.getClass().getName());
        }
        if (input.getUsername() == null || input.getUsername().equals("")) {
            output.generateErrorResponse("Github username is required!");
            throw new ApiErrorException(this.getClass().getName());
        }
        if (input.getSecretKey() == null || input.getSecretKey().equals("")) {
            output.generateErrorResponse("Github access token required!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }
}
