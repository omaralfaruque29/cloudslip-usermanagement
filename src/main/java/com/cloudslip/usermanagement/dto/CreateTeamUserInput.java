package com.cloudslip.usermanagement.dto;

import org.bson.types.ObjectId;

import java.io.Serializable;

public class CreateTeamUserInput extends BaseInputDTO  {

    private ObjectId id;
    private ObjectId teamId;
    private ObjectId userId;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getTeamId() {
        return teamId;
    }

    public void setTeamId(ObjectId teamId) {
        this.teamId = teamId;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }
}
