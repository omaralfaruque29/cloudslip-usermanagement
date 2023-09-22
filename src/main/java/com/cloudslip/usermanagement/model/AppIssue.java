package com.cloudslip.usermanagement.model;

import com.cloudslip.usermanagement.enums.IssueStatus;
import com.cloudslip.usermanagement.enums.IssueType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "app_issue")
public class AppIssue extends BaseEntity {

    private ObjectId applicationId;
    private ObjectId applicationTeamId;
    private String title;
    private String description;
    private List<User> taggedUserList;
    private IssueType issueType;
    private IssueStatus issueStatus;


    public String getApplicationId() {
        return applicationId.toHexString();
    }

    @JsonIgnore
    public ObjectId getApplicationObjectId() {
        return applicationId;
    }

    public void setApplicationId(ObjectId applicationId) {
        this.applicationId = applicationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonIgnore
    public String getApplicationTeamId() {
        return applicationTeamId.toHexString();
    }

    @JsonIgnore
    public ObjectId getApplicationTeamObjectId() {
        return applicationTeamId;
    }

    public void setApplicationTeamId(ObjectId applicationTeamId) {
        this.applicationTeamId = applicationTeamId;
    }

    public List<User> getTaggedUserList() {
        return taggedUserList;
    }

    public void setTaggedUserList(List<User> taggedUserList) {
        this.taggedUserList = taggedUserList;
    }

    public IssueType getIssueType() {
        return issueType;
    }

    public void setIssueType(IssueType issueType) {
        this.issueType = issueType;
    }

    public IssueStatus getIssueStatus() {
        return issueStatus;
    }

    public void setIssueStatus(IssueStatus issueStatus) {
        this.issueStatus = issueStatus;
    }
}
