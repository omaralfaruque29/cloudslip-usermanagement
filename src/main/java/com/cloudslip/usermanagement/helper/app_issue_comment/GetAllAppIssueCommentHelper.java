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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GetAllAppIssueCommentHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(GetAllAppIssueCommentHelper.class);

    private GetObjectInputDTO input;
    private ResponseDTO output = new ResponseDTO();

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
            output.generateErrorResponse("App Issue not found!");
            throw new ApiErrorException(this.getClass().getName());
        }
        appIssueTeam = teamRepository.findById(appIssue.get().getApplicationTeamObjectId());
        if (!this.validateUserAuthority()) {
            output.generateErrorResponse("User do not have access to access comment on the issue!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void doPerform() {
        List<AppIssueComment> appIssueCommentList = appIssueCommentRepository.findAllByAppIssueIdAndStatusAndParentAppIssueCommentIdIsNull(appIssue.get().getObjectId(), Status.V);
        output.generateSuccessResponse(appIssueCommentList, "All Comment List for App Issue");
    }



    protected void postPerformCheck() {
    }

    protected void doRollback() {

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
