package com.cloudslip.usermanagement.helper.app_issue_comment;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.GetObjectInputDTO;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.enums.Status;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.AppIssue;
import com.cloudslip.usermanagement.model.AppIssueComment;
import com.cloudslip.usermanagement.model.Team;
import com.cloudslip.usermanagement.repository.AppIssueCommentRepository;
import com.cloudslip.usermanagement.repository.AppIssueRepository;
import com.cloudslip.usermanagement.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class DeleteAppIssueCommentHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(DeleteAppIssueCommentHelper.class);

    private GetObjectInputDTO input;
    private ResponseDTO output = new ResponseDTO();

    private Optional<AppIssueComment> appIssueComment;
    private Optional<AppIssue> appIssue;
    private Optional<Team> appIssueTeam;

    @Autowired
    private AppIssueRepository appIssueRepository;

    @Autowired
    private AppIssueCommentRepository appIssueCommentRepository;

    @Autowired
    private TeamRepository teamRepository;

    public void init(BaseInput input, Object... extraParams) {
        this.input = (GetObjectInputDTO) input;
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
        appIssueComment = appIssueCommentRepository.findByIdAndStatus(input.getId(), Status.V);
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
            output.generateErrorResponse("User do not have access to delete comment on the issue!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void doPerform() {
        appIssueComment.get().setStatus(Status.D);
        appIssueComment.get().setLastUpdateActionId(actionId);
        appIssueComment.get().setUpdatedBy(requester.getUsername());
        appIssueComment.get().setUpdateDate(String.valueOf(LocalDateTime.now()));
        appIssueCommentRepository.save(appIssueComment.get());
        this.deleteChildComments();
        this.removeFromParentComment();
        output.generateSuccessResponse(null, "Comment Successfully Deleted");
    }


    protected void postPerformCheck() {
    }

    protected void doRollback() {

    }

    /*
        Remove All Child Comments
    */
    private void deleteChildComments() {
        if (appIssueComment.get().getChildComments() != null) {
            for (AppIssueComment appIssueComment : appIssueComment.get().getChildComments()) {
                appIssueComment.setStatus(Status.D);
                appIssueComment.setLastUpdateActionId(actionId);
                appIssueComment.setUpdatedBy(requester.getUsername());
                appIssueComment.setUpdateDate(String.valueOf(LocalDateTime.now()));
                appIssueCommentRepository.save(appIssueComment);
            }
        }
    }

    /*
        Remove from parent Comment
     */
    private void removeFromParentComment() {
        if (appIssueComment.get().getParentAppIssueCommentObjectId() != null) {
            Optional<AppIssueComment> parentAppIssueComment = appIssueCommentRepository.findByIdAndStatus(appIssueComment.get().getParentAppIssueCommentObjectId(), Status.V);
            if (parentAppIssueComment.isPresent()) {
                int appIssueCommentIndex = parentAppIssueComment.get().getChildCommentIndex(appIssueComment.get().getObjectId());
                if (appIssueCommentIndex != -1) { // index of comment in child comment list
                    parentAppIssueComment.get().getChildComments().remove(appIssueCommentIndex);
                    appIssueCommentRepository.save(parentAppIssueComment.get());
                }
            }
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
}
