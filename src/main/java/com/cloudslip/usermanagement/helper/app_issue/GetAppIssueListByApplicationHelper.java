package com.cloudslip.usermanagement.helper.app_issue;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.GetObjectInputDTO;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.enums.Status;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.AppIssue;
import com.cloudslip.usermanagement.model.Team;
import com.cloudslip.usermanagement.repository.AppIssueRepository;
import com.cloudslip.usermanagement.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GetAppIssueListByApplicationHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(GetAppIssueListByApplicationHelper.class);

    private GetObjectInputDTO input;
    private ResponseDTO output = new ResponseDTO();

    @Autowired
    private AppIssueRepository appIssueRepository;

    @Autowired
    private TeamRepository teamRepository;

    public void init(BaseInput input, Object... extraParams) {
        this.input = (GetObjectInputDTO) input;
        this.setOutput(output);
    }


    protected void checkPermission() {
        if (requester == null || requester.hasAuthority(Authority.ANONYMOUS)) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void checkValidity() {
    }


    protected void doPerform() {
        List<AppIssue> appIssueList = appIssueRepository.findAllByApplicationIdAndStatus(input.getId(), Status.V);
        if (!appIssueList.isEmpty()) {
            AppIssue appIssue = appIssueList.get(0);
            Optional<Team> applicationTeam = teamRepository.findById(appIssue.getApplicationTeamObjectId());
            if (!this.validateUserAuthority(applicationTeam.get())) {
                output.generateErrorResponse("User do not have access to get the application issue list!");
                throw new ApiErrorException(this.getClass().getName());
            }
        }
        output.generateSuccessResponse(appIssueList, "Application issue list response");
    }



    protected void postPerformCheck() {
    }

    protected void doRollback() {

    }

    /*
        validate requester
    */
    private boolean validateUserAuthority(Team applicationTeam) {
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
}
