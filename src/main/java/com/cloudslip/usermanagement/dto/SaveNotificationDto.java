package com.cloudslip.usermanagement.dto;

public class SaveNotificationDto extends BaseInputDTO {

    private String text;
    private String type;
    private String email;

    public SaveNotificationDto() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

