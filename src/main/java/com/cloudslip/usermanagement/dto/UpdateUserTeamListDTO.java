package com.cloudslip.usermanagement.dto;

import com.cloudslip.usermanagement.model.Team;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.List;

public class UpdateUserTeamListDTO extends BaseInputDTO {

    private ObjectId userId;
    private List<Team> teams;

    public UpdateUserTeamListDTO() {

    }

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }
}
