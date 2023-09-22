package com.cloudslip.usermanagement.helper.app_issue;

import com.cloudslip.usermanagement.dto.appIsssue.UpdateAppIssueDTO;
import com.cloudslip.usermanagement.dto.BaseInput;
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
import com.cloudslip.usermanagement.repository.TeamRepository;
import com.cloudslip.usermanagement.repository.UserInfoRepository;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UpdateAppIssueHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(UpdateAppIssueHelper.class);

    private UpdateAppIssueDTO input;
    private ResponseDTO output = new ResponseDTO();

    private Optional<AppIssue> appIssue;
    private Optional<Team> applicationTeam;

    @Autowired
    private AppIssueRepository appIssueRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;


    public void init(BaseInput input, Object... extraParams) {
        this.input = (UpdateAppIssueDTO) input;
        this.setOutput(output);
        appIssue = null;
    }


    protected void checkPermission() {
        if (requester == null || requester.hasAuthority(Authority.ANONYMOUS)) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void checkValidity() {
        this.validateRequiredFields();
        this.validateEnums();
        this.validateTaggedUsers();
        appIssue = appIssueRepository.findByIdAndStatus(input.getAppIssueId(), Status.V);
        if (!appIssue.isPresent()) {
            output.generateErrorResponse("App issue not found!");
            throw new ApiErrorException(this.getClass().getName());
        }
        applicationTeam = teamRepository.findById(appIssue.get().getApplicationTeamObjectId());
        if (!this.validateUserAuthority()) {
            output.generateErrorResponse("User do not have access to update the application issue!");
            throw new ApiErrorException(this.getClass().getName());
        }
        this.validateTaggedUsers();
    }


    protected void doPerform() {
        appIssue.get().setTitle(input.getTitle());
        appIssue.get().setDescription(input.getDescription() == null ? "" : input.getDescription());
        appIssue.get().setTaggedUserList(input.getTaggedUserList());
        appIssue.get().setIssueType(IssueType.valueOf(input.getIssueType()));
        appIssue.get().setIssueStatus(IssueStatus.valueOf(input.getIssueStatus()));

        appIssue.get().setUpdatedBy(requester.getUsername());
        appIssue.get().setUpdateDate(String.valueOf(LocalDateTime.now()));
        appIssue.get().setLastUpdateActionId(actionId);
        AppIssue savedAppIssue = appIssueRepository.save(appIssue.get());
        output.generateSuccessResponse(savedAppIssue, "Application issue successfully updated");
    }



    protected void postPerformCheck() {
    }

    protected void doRollback() {

    }

    /*
        Validating the required fields of input
    */
    private void validateRequiredFields() {
        if (input.getAppIssueId() == null) {
            output.generateErrorResponse("App Issue id required!");
            throw new ApiErrorException(this.getClass().getName());
        }
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
            return requester.getCompanyId().toString().equals(applicationTeam.get().getCompanyId());
        } else if (!requester.hasAuthority(Authority.ROLE_SUPER_ADMIN) && !requester.hasAuthority(Authority.ROLE_ADMIN)) {
            return applicationTeam.get().existInTeamIdList(requester.getTeamIdList());
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
            return applicationTeam.get().getCompanyId().equals(user.getCompanyId().toString());
        } else if (!user.hasAuthority(Authority.ROLE_ADMIN) && !user.hasAuthority(Authority.ROLE_SUPER_ADMIN)) {
            Optional<UserInfo> userInfo = userInfoRepository.findByUserId(user.getObjectId());
            if (userInfo.get().getTeams() == null) {
                return false;
            }
            return userInfo.get().hasTeam(applicationTeam.get().getObjectId());
        }
        return true;
    }
}
