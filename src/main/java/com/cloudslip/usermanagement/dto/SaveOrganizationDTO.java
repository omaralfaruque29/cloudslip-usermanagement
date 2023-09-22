package com.cloudslip.usermanagement.dto;

import com.cloudslip.usermanagement.model.dummy.GitDirectory;
import org.bson.types.ObjectId;

public class SaveOrganizationDTO extends BaseInputDTO {

    private String name;
    private String description;
    private ObjectId companyId;
    private GitDirectory gitDirectory;

    public SaveOrganizationDTO() {
    }

    public SaveOrganizationDTO(String name, String description, ObjectId companyId, GitDirectory gitDirectory) {
        this.name = name;
        this.description = description;
        this.companyId = companyId;
        this.gitDirectory = gitDirectory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ObjectId getCompanyId() {
        return companyId;
    }

    public void setCompanyId(ObjectId companyId) {
        this.companyId = companyId;
    }

    public GitDirectory getGitDirectory() {
        return gitDirectory;
    }

    public void setGitDirectory(GitDirectory gitDirectory) {
        this.gitDirectory = gitDirectory;
    }
}
