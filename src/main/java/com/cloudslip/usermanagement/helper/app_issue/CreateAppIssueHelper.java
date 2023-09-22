package com.cloudslip.usermanagement.helper.app_issue;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.appIsssue.CreateAppIssueDTO;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.enums.IssueStatus;
import com.cloudslip.usermanagement.enums.IssueType;
import com.cloudslip.usermanagement.enums.Status;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.AppIssue;
import com.cloudslip.usermanagement.model.Team;
import com.cloudslip.usermanagement.model.User;
import com.cloudslip.usermanagement.model.UserInfo;
import com.cloudslip.usermanagement.repository.AppIssueRepository;
import com.cloudslip.usermanagement.repository.UserInfoRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CreateAppIssueHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(CreateAppIssueHelper.class);

    private CreateAppIssueDTO input;
    private ResponseDTO output = new ResponseDTO();

    private ResponseDTO response = new ResponseDTO();
    private Team applicationTeam;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AppIssueRepository appIssueRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    public void init(BaseInput input, Object... extraParams) {
        this.input = (CreateAppIssueDTO) input;
        this.setOutput(output);
        this.response = (ResponseDTO) extraParams[0];
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.applicationTeam = objectMapper.convertValue(response.getData(), new TypeReference<Team>() { });
    }


    protected void checkPermission() {
        if (requester == null || requester.hasAuthority(Authority.ANONYMOUS)) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void checkValidity() {
        if (!this.validateUserAuthority()) {
            output.generateErrorResponse("User do not have access to update the application issue!");
            throw new ApiErrorException(this.getClass().getName());
        }
        this.validateRequiredFields();
        this.validateEnums();
        this.validateTaggedUsers();
    }

    protected void doPerform() {
        AppIssue appIssue = new AppIssue();
        appIssue.setApplicationId(input.getApplicationId());
        appIssue.setTitle(input.getTitle());
        appIssue.setDescription(input.getDescription() == null ? "" : input.getDescription());
        appIssue.setTaggedUserList(input.getTaggedUserList());
        appIssue.setIssueType(IssueType.valueOf(input.getIssueType()));
        appIssue.setIssueStatus(IssueStatus.valueOf(input.getIssueStatus()));
        appIssue.setApplicationTeamId(applicationTeam.getObjectId());

        appIssue.setStatus(Status.V);
        appIssue.setCreatedBy(requester.getUsername());
        appIssue.setCreateDate(String.valueOf(LocalDateTime.now()));
        appIssue.setCreateActionId(actionId);
        appIssue = appIssueRepository.save(appIssue);
        output.generateSuccessResponse(appIssue, "An issue for the application has successfully uploaded");
    }


    protected void postPerformCheck() {
    }

    protected void doRollback() {

    }

    /*
        Validating the required fields of input
    */
    private void validateRequiredFields() {
        if (input.getTitle() == null || input.getTitle().equals("")) {
            output.generateErrorResponse("Title of the issue is required!");
            throw new ApiErrorException(this.getClass().getName());
        }
        if (input.getIssueType() == null) {
            output.generateErrorResponse("Issue type is required!");
            throw new ApiErrorException(this.getClass().getName());
        }
        if (input.getIssueStatus() == null) {
            output.generateErrorResponse("Issue status is required!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }

    /*
        Check the input issue type and issue status whether these input exists in appropriate enum classes
     */
    private void validateEnums() {
        if (!EnumUtils.isValidEnum(IssueType.class, input.getIssueType())) {
            output.generateErrorResponse("Invalid issue type!");
            throw new ApiErrorException(this.getClass().getName());
        } else if (!EnumUtils.isValidEnum(IssueStatus.class, input.getIssueStatus())) {
            output.generateErrorResponse("Invalid issue status!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }

    /*
        validate requester
    */
    private boolean validateUserAuthority() {
        if (requester.hasAuthority(Authority.ROLE_ADMIN) && !requester.hasAuthority(Authority.ROLE_SUPER_ADMIN)) {
            return requester.getCompanyId().toString().equals(applicationTeam.getCompanyId());
        } else if (!requester.hasAuthority(Authority.ROLE_SUPER_ADMIN) && !requester.hasAuthority(Authority.ROLE_ADMIN)) {
            if (requester.getTeamIdList() == null) {
                return false;
            }
            return applicationTeam.existInTeamIdList(requester.getTeamIdList());
        }
        return true;
    }

    /*
        validate the input user id list whether user exists with the id or not
    */
    private void validateTaggedUsers() {
        for (User user : input.getTaggedUserList()) {
            if (!this.validateTaggedUserHasAuthority(user)) {
                output.generateErrorResponse(String.format("User '%s %s' cannot be tagged", user.getUserInfo().getFirstName(), user.getUserInfo().getLastName()));
                throw new ApiErrorException(this.getClass().getName());
            }
        }
    }

    /*
        validate tagged user authority
     */
    private boolean validateTaggedUserHasAuthority(User user) {
        if (user.hasAuthority(Authority.ROLE_ADMIN) &&!user.hasAuthority(Authority.ROLE_SUPER_ADMIN)) {
            return applicationTeam.getCompanyId().equals(user.getCompanyId().toString());
        } else if (!user.hasAuthority(Authority.ROLE_ADMIN) && !user.hasAuthority(Authority.ROLE_SUPER_ADMIN)) {
            Optional<UserInfo> userInfo = userInfoRepository.findByUserId(user.getObjectId());
            if (userInfo.get().getTeams() == null) {
                return false;
            }
            return userInfo.get().hasTeam(applicationTeam.getObjectId());
        }
        return true;
    }
}
