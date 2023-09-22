package com.cloudslip.usermanagement.helper.app_issue;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.GetObjectInputDTO;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.enums.ResponseStatus;
import com.cloudslip.usermanagement.enums.Status;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.AppIssue;
import com.cloudslip.usermanagement.model.AppIssueComment;
import com.cloudslip.usermanagement.model.Team;
import com.cloudslip.usermanagement.repository.AppIssueCommentRepository;
import com.cloudslip.usermanagement.repository.AppIssueRepository;
import com.cloudslip.usermanagement.repository.TeamRepository;
import com.cloudslip.usermanagement.service.AppIssueCommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DeleteAppIssueHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(GetAppIssueListByApplicationHelper.class);

    private GetObjectInputDTO input;
    private ResponseDTO output = new ResponseDTO();

    private Optional<AppIssue> appIssue;
    private Optional<Team> applicationTeam;

    @Autowired
    private AppIssueRepository appIssueRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private AppIssueCommentService appIssueCommentService;

    @Autowired
    private AppIssueCommentRepository appIssueCommentRepository;

    public void init(BaseInput input, Object... extraParams) {
        this.input = (GetObjectInputDTO) input;
        this.setOutput(output);
        appIssue = null;
        applicationTeam = null;
    }


    protected void checkPermission() {
        if (requester == null || requester.hasAuthority(Authority.ANONYMOUS)) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void checkValidity() {
        appIssue = appIssueRepository.findByIdAndStatus(input.getId(), Status.V);
        if (!appIssue.isPresent()) {
            output.generateErrorResponse("App issue not found!");
            throw new ApiErrorException(this.getClass().getName());
        }
        applicationTeam = teamRepository.findById(appIssue.get().getApplicationTeamObjectId());
        if (!this.validateUserAuthority()) {
            output.generateErrorResponse("User do not have access to delete the application issue!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void doPerform() {
        this.deleteAllComments();
        appIssue.get().setStatus(Status.D);
        appIssue.get().setUpdatedBy(requester.getUsername());
        appIssue.get().setUpdateDate(String.valueOf(LocalDateTime.now()));
        appIssue.get().setLastUpdateActionId(actionId);
        appIssueRepository.save(appIssue.get());
        output.generateSuccessResponse(null, "Application issue successfully deleted");
    }

    protected void postPerformCheck() {
    }

    protected void doRollback() {
        if (appIssue != null && appIssue.isPresent()) {
            appIssue.get().setStatus(Status.V);
            appIssueRepository.save(appIssue.get());
        }
    }

    /*
        validate requester
    */
    private boolean validateUserAuthority() {
        if (requester.hasAuthority(Authority.ROLE_ADMIN) && !requester.hasAuthority(Authority.ROLE_SUPER_ADMIN)) {
            return requester.getCompanyId().toString().equals(applicationTeam.get().getCompanyId());
        } else if (!requester.hasAuthority(Authority.ROLE_SUPER_ADMIN) && !requester.hasAuthority(Authority.ROLE_ADMIN)) {
            if (requester.getTeamIdList() == null) {
                return false;
            }
            return applicationTeam.get().existInTeamIdList(requester.getTeamIdList());
        }
        return true;
    }

    private void deleteAllComments() {
        List<AppIssueComment> appIssueCommentList = appIssueCommentRepository.findAllByAppIssueIdAndStatusAndParentAppIssueCommentIdIsNull(appIssue.get().getObjectId(), Status.V);
        for (AppIssueComment appIssueComment : appIssueCommentList) {
            ResponseDTO responseDTO = appIssueCommentService.delete(appIssueComment.getObjectId(), requester, actionId);
            if (responseDTO.getStatus() == ResponseStatus.error) {
                output.generateErrorResponse("Could not remove Comments!");
                throw new ApiErrorException(this.getClass().getName(), true);
            }
        }
    }
}
