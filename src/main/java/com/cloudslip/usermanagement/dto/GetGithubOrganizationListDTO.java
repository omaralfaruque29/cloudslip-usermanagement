package com.cloudslip.usermanagement.dto;

import com.cloudslip.usermanagement.model.dummy.GitDirectory;

import java.io.Serializable;
import java.util.ArrayList;

public class GetGithubOrganizationListDTO implements Serializable {

    private int status;
    private ArrayList<GitDirectory> body = new ArrayList<>();

    public GetGithubOrganizationListDTO() {
    }

    public GetGithubOrganizationListDTO(int status, ArrayList<GitDirectory> body) {
        this.status = status;
        this.body = body;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ArrayList<GitDirectory> getBody() {
        return body;
    }

    public void setBody(ArrayList<GitDirectory> body) {
        this.body = body;
    }
}
