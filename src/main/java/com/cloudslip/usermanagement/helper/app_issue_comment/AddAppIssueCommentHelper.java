package com.cloudslip.usermanagement.helper.app_issue_comment;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.dto.app_issue_comment.AddAppIssueCommentDTO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AddAppIssueCommentHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(AddAppIssueCommentHelper.class);

    private AddAppIssueCommentDTO input;
    private ResponseDTO output = new ResponseDTO();

    private Optional<AppIssue> appIssue;
    private Optional<Team> appIssueTeam;
    private Optional<AppIssueComment> parentAppIssueComment;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private AppIssueRepository appIssueRepository;

    @Autowired
    private AppIssueCommentRepository appIssueCommentRepository;

    @Autowired
    private TeamRepository teamRepository;

    public void init(BaseInput input, Object... extraParams) {
        this.input = (AddAppIssueCommentDTO) input;
        this.setOutput(output);
        appIssue = null;
        appIssueTeam = null;
        parentAppIssueComment = null;
    }


    protected void checkPermission() {
        if (requester == null || requester.hasAuthority(Authority.ANONYMOUS)) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void checkValidity() {
        this.validateRequiredFields();
        appIssue = appIssueRepository.findByIdAndStatus(input.getAppIssueId(), Status.V);
        if (!appIssue.isPresent()) {
            output.generateErrorResponse("App Issue not found!");
            throw new ApiErrorException(this.getClass().getName());
        }
        appIssueTeam = teamRepository.findById(appIssue.get().getApplicationTeamObjectId());
        if (!this.validateUserAuthority()) {
            output.generateErrorResponse("User do not have access to add comment on the issue!");
            throw new ApiErrorException(this.getClass().getName());
        }

        this.validateTaggedUsers();

        if (input.getParentAppIssueCommentId() != null) {
            parentAppIssueComment = appIssueCommentRepository.findByIdAndStatus(input.getParentAppIssueCommentId(), Status.V);
            if (!parentAppIssueComment.isPresent()) {
                output.generateErrorResponse("App Issue Comment not found!");
                throw new ApiErrorException(this.getClass().getName());
            }
            if (parentAppIssueComment.get().getParentAppIssueCommentObjectId() != null) {
                output.generateErrorResponse("Cannot add nested issue comments");
                throw new ApiErrorException(this.getClass().getName());
            }
        }
    }


    protected void doPerform() {
        AppIssueComment appIssueComment = new AppIssueComment();
        appIssueComment.setAppIssueId(input.getAppIssueId());
        appIssueComment.setComment(input.getComment());
        appIssueComment.setTaggedUserList(input.getTaggedUserList());
        if (parentAppIssueComment != null) {
            appIssueComment.setParentAppIssueCommentId(parentAppIssueComment.get().getObjectId());
        }
        appIssueComment.setStatus(Status.V);
        appIssueComment.setCreatedBy(requester.getUsername());
        appIssueComment.setCreateDate(String.valueOf(LocalDateTime.now()));
        appIssueComment.setCreateActionId(actionId);
        appIssueComment = appIssueCommentRepository.save(appIssueComment);
        if (appIssueComment.getParentAppIssueCommentObjectId() != null) { // if comment has parent
            List<AppIssueComment> childCommentList = parentAppIssueComment.get().getChildComments() == null ? new ArrayList<>() : parentAppIssueComment.get().getChildComments();
            childCommentList.add(appIssueComment);
            parentAppIssueComment.get().setChildComments(childCommentList);
            appIssueCommentRepository.save(parentAppIssueComment.get());
        }
        output.generateSuccessResponse(appIssueComment.getId(), "An issue Comment successfully added");
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
