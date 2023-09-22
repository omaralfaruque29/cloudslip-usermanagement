package com.cloudslip.usermanagement.dto;


public class CreateClusterAgentServiceUserDTO extends BaseInputDTO {
    private static final long serialVersionUID = 7954325925563724664L;

    private String username;
    private String kafkaTopic;

    public CreateClusterAgentServiceUserDTO() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getKafkaTopic() {
        return kafkaTopic;
    }

    public void setKafkaTopic(String kafkaTopic) {
        this.kafkaTopic = kafkaTopic;
    }
}
