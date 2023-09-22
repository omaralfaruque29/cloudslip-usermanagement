package com.cloudslip.usermanagement.helper.application;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.GetObjectInputDTO;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.Team;
import com.cloudslip.usermanagement.model.UserInfo;
import com.cloudslip.usermanagement.repository.TeamRepository;
import com.cloudslip.usermanagement.repository.UserInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetTeamHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(GetTeamHelper.class);

    private GetObjectInputDTO input;
    private ResponseDTO output = new ResponseDTO();

    @Autowired
    UserInfoRepository userInfoRepository;

    @Autowired
    TeamRepository teamRepository;

    private Optional<Team> team;

    public void init(BaseInput input, Object... extraParams) {
        this.input = (GetObjectInputDTO) input;
        this.setOutput(output);
        team = teamRepository.findById(this.input.getId());
    }


    protected void checkPermission() {
        if (requester == null || requester.hasAuthority(Authority.ANONYMOUS)) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void checkValidity() {
        String teamExists = "";
        if (!team.isPresent()) {
            output.generateErrorResponse("Team Not Found!");
            throw new ApiErrorException(this.getClass().getName());
        }
        this.checkTeam();
    }

    protected void doPerform() {
        output.generateSuccessResponse(team.get());
    }



    protected void postPerformCheck() {
    }

    protected void doRollback() {

    }

    private void checkTeam() {
        if (requester.hasAuthority(Authority.ROLE_ADMIN) &&!requester.hasAuthority(Authority.ROLE_SUPER_ADMIN)) {
            if (!team.get().getCompanyId().equals(requester.getCompanyId().toString())) {
                output.generateErrorResponse("User do not have authority to access the team!");
                throw new ApiErrorException(this.getClass().getName());
            }
        } else if (!requester.hasAuthority(Authority.ROLE_ADMIN) && !requester.hasAuthority(Authority.ROLE_SUPER_ADMIN)) {
            if (requester.getTeamIdList() == null) {
                output.generateErrorResponse("User do not have any team!");
                throw new ApiErrorException(this.getClass().getName());
            }
            if (!team.get().existInTeamIdList(requester.getTeamIdList())) {
                output.generateErrorResponse("User do not have authority to access the team!");
                throw new ApiErrorException(this.getClass().getName());
            }
        }
    }
}
