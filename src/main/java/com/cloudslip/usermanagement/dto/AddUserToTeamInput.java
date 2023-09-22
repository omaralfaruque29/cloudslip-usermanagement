package com.cloudslip.usermanagement.dto;

import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.List;

public class AddUserToTeamInput implements Serializable {

    private ObjectId teamId;
    private List<ObjectId> userIdList;

    public ObjectId getTeamId() {
        return teamId;
    }

    public void setTeamId(ObjectId teamId) {
        this.teamId = teamId;
    }

    public List<ObjectId> getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(List<ObjectId> userIdList) {
        this.userIdList = userIdList;
    }
}
