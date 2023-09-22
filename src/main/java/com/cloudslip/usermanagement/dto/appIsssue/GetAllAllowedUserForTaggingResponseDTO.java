package com.cloudslip.usermanagement.dto.appIsssue;

import com.cloudslip.usermanagement.model.Team;
import com.cloudslip.usermanagement.model.UserInfo;

import java.util.List;

public class GetAllAllowedUserForTaggingResponseDTO {
    private List<UserInfo> userInfoList;
    private Team applicationTeam;

    public GetAllAllowedUserForTaggingResponseDTO() {
    }

    public GetAllAllowedUserForTaggingResponseDTO(List<UserInfo> userInfoList, Team applicationTeam) {
        this.userInfoList = userInfoList;
        this.applicationTeam = applicationTeam;
    }

    public List<UserInfo> getUserInfoList() {
        return userInfoList;
    }

    public void setUserInfoList(List<UserInfo> userInfoList) {
        this.userInfoList = userInfoList;
    }

    public Team getApplicationTeam() {
        return applicationTeam;
    }

    public void setApplicationTeam(Team applicationTeam) {
        this.applicationTeam = applicationTeam;
    }
}
