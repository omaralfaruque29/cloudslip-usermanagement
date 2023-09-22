package com.cloudslip.usermanagement.model;

public class Recipient {

    private UserInfo user;
    private boolean seen = false;
    private String seenAt;

    public Recipient() {
    }

    public Recipient(UserInfo user) {
        this.user = user;
        this.seen = false;
        this.seenAt = null;
    }

    public Recipient(UserInfo user, boolean seen, String seenAt) {
        this.user = user;
        this.seen = seen;
        this.seenAt = seenAt;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getSeenAt() {
        return seenAt;
    }

    public void setSeenAt(String seenAt) {
        this.seenAt = seenAt;
    }
}
