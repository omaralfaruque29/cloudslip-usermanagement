package com.cloudslip.usermanagement.model;

import com.cloudslip.usermanagement.enums.NotificationType;
import org.bson.types.ObjectId;

public class Notification extends BaseEntity {

    private String text;
    private String redirectUrl;
    private ObjectId userId;
    private boolean clicked = false;
    private String clickedAt;
    private String type;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public String getClickedAt() {
        return clickedAt;
    }

    public void setClickedAt(String clickedAt) {
        this.clickedAt = clickedAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
