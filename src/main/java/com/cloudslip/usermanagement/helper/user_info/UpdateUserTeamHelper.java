package com.cloudslip.usermanagement.helper.user_info;

import com.cloudslip.usermanagement.dto.*;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.Team;
import com.cloudslip.usermanagement.model.UserInfo;
import com.cloudslip.usermanagement.repository.TeamRepository;
import com.cloudslip.usermanagement.repository.UserInfoRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UpdateUserTeamHelper extends AbstractHelper {
    private final Logger log = LoggerFactory.getLogger(UpdateUserTeamHelper.class);

    private UpdateUserTeamListDTO input;

    @Autowired
    private TeamRepository teamRepository;

    private ResponseDTO output = new ResponseDTO();

    @Autowired
    private final UserInfoRepository userInfoRepository;


    public UpdateUserTeamHelper(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }


    public void init(BaseInput input, Object... extraParams) {
        this.input = (UpdateUserTeamListDTO) input;
        this.setOutput(output);

        if(extraParams.length > 0) {
            ObjectId actionId = (ObjectId) extraParams[0];
            System.out.println(actionId.toHexString());
        }
    }


    protected void checkPermission() {
        if (requester == null || (!requester.hasAuthority(Authority.ROLE_SUPER_ADMIN) && !requester.hasAuthority(Authority.ROLE_ADMIN))) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void checkValidity() {
    }


    protected void doPerform() {
        UserInfo existingUser = userInfoRepository.findById(input.getUserId()).get();
        List<Team> teamList = new ArrayList<>();
        if(input != null && input.getTeams() != null && input.getTeams().size() > 0) {
            List<Team> tempTeamList = input.getTeams();

            for(int i = 0; i < tempTeamList.size(); i++) {
                Team team = teamRepository.findById(new ObjectId(tempTeamList.get(i).getId())).get();
                if(team != null) {
                    teamList.add(team);
                }
            }
        }
        existingUser.setTeams(teamList);
        existingUser.setUpdateDate(String.valueOf(LocalDateTime.now()));
        output.generateSuccessResponse(userInfoRepository.save(existingUser));

    }

    protected void postPerformCheck() {
    }

    protected void doRollback() {

    }
}
