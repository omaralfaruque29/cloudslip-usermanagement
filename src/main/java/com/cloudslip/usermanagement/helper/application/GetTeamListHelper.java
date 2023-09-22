package com.cloudslip.usermanagement.helper.application;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.dto.UserInfoResponseDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.Team;
import com.cloudslip.usermanagement.model.UserInfo;
import com.cloudslip.usermanagement.repository.TeamRepository;
import com.cloudslip.usermanagement.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GetTeamListHelper extends AbstractHelper {

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    UserInfoRepository userInfoRepository;

    private ResponseDTO output = new ResponseDTO();

    public void init(BaseInput input, Object... extraParams) {
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
        Optional<UserInfo> userInfo = userInfoRepository.findByUserId(requester.getObjectId());
        if (!userInfo.isPresent()) {
            output.generateErrorResponse("User Info Not Found!");
            throw new ApiErrorException(this.getClass().getName());
        }
        List<Team> teamList = null;
        if(requester.hasAuthority(Authority.ROLE_ADMIN) && !requester.hasAuthority(Authority.ROLE_SUPER_ADMIN)) {
            teamList = teamRepository.findAllByCompanyId(userInfo.get().getCompany().getObjectId());
        } else if (!requester.hasAuthority(Authority.ROLE_ADMIN) && !requester.hasAuthority(Authority.ROLE_SUPER_ADMIN)) {
            teamList = userInfo.get().getTeams();
        }
        output.generateSuccessResponse(teamList);
    }



    protected void postPerformCheck() {
    }

    protected void doRollback() {

    }
}
