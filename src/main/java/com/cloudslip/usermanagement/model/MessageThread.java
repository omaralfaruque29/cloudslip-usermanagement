package com.cloudslip.usermanagement.model;

import java.util.List;

public class MessageThread extends BaseEntity {

    private String title;
    private List<UserInfo> userList;

    public MessageThread() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<UserInfo> getUserList() {
        return userList;
    }

    public void setUserList(List<UserInfo> userList) {
        this.userList = userList;
    }
}
