package com.cloudslip.usermanagement.helper.app_issue_comment;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.dto.app_issue_comment.UpdateAppIssueCommentDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.enums.Status;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.*;
import com.cloudslip.usermanagement.repository.AppIssueCommentRepository;
import com.cloudslip.usermanagement.repository.AppIssueRepository;
import com.cloudslip.usermanagement.repository.TeamRepository;
import com.cloudslip.usermanagement.repository.UserInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UpdateAppIssueCommentHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(UpdateAppIssueCommentHelper.class);

    private UpdateAppIssueCommentDTO input;
    private ResponseDTO output = new ResponseDTO();

    private Optional<AppIssueComment> appIssueComment;
    private Optional<AppIssue> appIssue;
    private Optional<Team> appIssueTeam;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private AppIssueRepository appIssueRepository;

    @Autowired
    private AppIssueCommentRepository appIssueCommentRepository;

    @Autowired
    private TeamRepository teamRepository;

    public void init(BaseInput input, Object... extraParams) {
        this.input = (UpdateAppIssueCommentDTO) input;
        this.setOutput(output);
        appIssue = null;
        appIssueTeam = null;
        appIssueComment = null;
    }


    protected void checkPermission() {
        if (requester == null || requester.hasAuthority(Authority.ANONYMOUS)) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void checkValidity() {
        this.validateRequiredFields();
        appIssueComment = appIssueCommentRepository.findByIdAndStatus(input.getAppIssueCommentId(), Status.V);
        if (!appIssueComment.isPresent()) {
            output.generateErrorResponse("App issue comment not found!");
            throw new ApiErrorException(this.getClass().getName());
        }

        appIssue = appIssueRepository.findByIdAndStatus(appIssueComment.get().getAppIssueObjectId(), Status.V);
        if (!appIssue.isPresent()) {
            output.generateErrorResponse("App Issue not found!");
            throw new ApiErrorException(this.getClass().getName());
        }
        appIssueTeam = teamRepository.findById(appIssue.get().getApplicationTeamObjectId());
        if (!this.validateUserAuthority()) {
            output.generateErrorResponse("User do not have access to update comment on the issue!");
            throw new ApiErrorException(this.getClass().getName());
        }
        this.validateTaggedUsers();
    }


    protected void doPerform() {
        appIssueComment.get().setComment(input.getComment());
        appIssueComment.get().setTaggedUserList(input.getTaggedUserList());
        appIssueComment.get().setUpdatedBy(requester.getUsername());
        appIssueComment.get().setUpdateDate(String.valueOf(LocalDateTime.now()));
        appIssueComment.get().setLastUpdateActionId(actionId);
        AppIssueComment savedAppIssueComment = appIssueCommentRepository.save(appIssueComment.get());
        if (appIssueComment.get().getParentAppIssueCommentObjectId() != null) {
            Optional<AppIssueComment> parentAppIssueComment = appIssueCommentRepository.findByIdAndStatus(appIssueComment.get().getParentAppIssueCommentObjectId(), Status.V);
            int appIssueCommentIndex = parentAppIssueComment.get().getChildCommentIndex(appIssueComment.get().getObjectId());
            if (appIssueCommentIndex != -1) { // index of comment in child comment list
                parentAppIssueComment.get().getChildComments().set(appIssueCommentIndex, savedAppIssueComment);
            }
            appIssueCommentRepository.save(parentAppIssueComment.get());
        }
        output.generateSuccessResponse(savedAppIssueComment.getId(), "An issue Comment successfully updated");
    }



    protected void postPerformCheck() {
    }

    protected void doRollback() {

    }

    /*
        Validating the required fields of input
    */
    private void validateRequiredFields() {
        if (input.getAppIssueCommentId() == null) {
            output.generateErrorResponse("App Issue Id is Required!");
            throw new ApiErrorException(this.getClass().getName());
        }
        if (input.getComment() == null || input.getComment().equals("")) {
            output.generateErrorResponse("App Issue Comment is required!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }

    /*
        validate requester
    */
    private boolean validateUserAuthority() {
        if (requester.hasAuthority(Authority.ROLE_ADMIN) && !requester.hasAuthority(Authority.ROLE_SUPER_ADMIN)) {
            return requester.getCompanyId().toString().equals(appIssueTeam.get().getCompanyId());
        } else if (!requester.hasAuthority(Authority.ROLE_SUPER_ADMIN) && !requester.hasAuthority(Authority.ROLE_ADMIN)) {
            if (requester.getTeamIdList() == null) {
                return false;
            }
            return appIssueTeam.get().existInTeamIdList(requester.getTeamIdList());
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
            return appIssueTeam.get().getCompanyId().equals(user.getCompanyId().toString());
        } else if (!user.hasAuthority(Authority.ROLE_ADMIN) && !user.hasAuthority(Authority.ROLE_SUPER_ADMIN)) {
            Optional<UserInfo> userInfo = userInfoRepository.findByUserId(user.getObjectId());
            if (userInfo.get().getTeams() == null) {
                return false;
            }
            return userInfo.get().hasTeam(appIssueTeam.get().getObjectId());
        }
        return true;
    }
}
