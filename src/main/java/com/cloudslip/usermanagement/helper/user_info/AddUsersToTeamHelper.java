package com.cloudslip.usermanagement.helper.user_info;

import com.cloudslip.usermanagement.dto.*;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.Company;
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
import java.util.Optional;

@Service
public class AddUsersToTeamHelper extends AbstractHelper {
    private final Logger log = LoggerFactory.getLogger(AddUsersToTeamHelper.class);

    private SaveUsersToTeamDTO input;

    private ResponseDTO output = new ResponseDTO();

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    public AddUsersToTeamHelper(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }


    public void init(BaseInput input, Object... extraParams) {
        this.input = (SaveUsersToTeamDTO) input;
        this.setOutput(output);
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
        Optional<Team> team = teamRepository.findById(input.getTeamId());
        if(!team.isPresent()) {
            output.generateErrorResponse(String.format("Team doesn't exist with the name - %s", team.get().getName()));
        }
        List<UserInfo> userInfoList = new ArrayList<>();
        if(input.getUserList() != null && input.getUserList().size() > 0) {
            List<ObjectId> tempUserList = input.getUserList();
            for(int i = 0; i < tempUserList.size(); i++) {
                UserInfo user = userInfoRepository.findByUserId(tempUserList.get(i)).get();
                if(user != null) {
                    if(user.getTeams() == null){
                        List<Team> teamList = new ArrayList<>();
                        teamList.add(team.get());
                        user.setTeams(teamList);
                        user.setUpdatedBy(requester.getUsername());
                        user.setUpdateDate(String.valueOf(LocalDateTime.now()));
                        user = userInfoRepository.save(user);
                        userInfoList.add(user);
                    } else if(user.getTeams() != null){
                        List<Team> existingTeamList = new ArrayList<>();
                        existingTeamList = user.getTeams();
                        for(int j = 0; j < existingTeamList.size(); j++)
                            if (!(team.get().getId().equals(existingTeamList.get(j).getId()))) {
                                continue;
                            } else {
                                output.generateErrorResponse(String.format("Already User - %s  is assigned to team %s", user.getFirstName(), team.get().getName()));
                                throw new ApiErrorException(this.getClass().getName());
                            }
                        existingTeamList.add(team.get());
                        user.setTeams(existingTeamList);
                        user.setUpdatedBy(requester.getUsername());
                        user.setUpdateDate(String.valueOf(LocalDateTime.now()));
                        user = userInfoRepository.save(user);
                        userInfoList.add(user);
                    } else {
                        output.generateErrorResponse(String.format("User - %s is not assigned under any team", user.getFirstName()));
                        throw new ApiErrorException(this.getClass().getName());
                    }

                } else {
                    output.generateErrorResponse(String.format("Invalid User with the email - %s", user.getEmail()));
                    throw new ApiErrorException(this.getClass().getName());
                }
            }
        }
        else {
            output.generateErrorResponse(String.format("No Team found with the id - %s", team.get().getName()));
            throw new ApiErrorException(this.getClass().getName());
        }
        output.generateSuccessResponse(userInfoList, String.format("Users are added to team %s", team.get().getName()));

    }




    protected void postPerformCheck() {
    }

    protected void doRollback() {

    }
}
