package com.cloudslip.usermanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "app_issue_comment")
public class AppIssueComment extends BaseEntity {

    private ObjectId appIssueId;
    private String comment;
    private ObjectId parentAppIssueCommentId;
    private List<AppIssueComment> childComments;
    private List<User> taggedUserList;

    public AppIssueComment() {
    }

    public String getAppIssueId() {
        return appIssueId.toHexString();
    }

    @JsonIgnore
    public ObjectId getAppIssueObjectId() {
        return appIssueId;
    }

    public void setAppIssueId(ObjectId appIssueId) {
        this.appIssueId = appIssueId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getParentAppIssueCommentId() {
        if (parentAppIssueCommentId == null) {
            return null;
        }
        return parentAppIssueCommentId.toHexString();
    }

    @JsonIgnore
    public ObjectId getParentAppIssueCommentObjectId() {
        return parentAppIssueCommentId;
    }

    public void setParentAppIssueCommentId(ObjectId parentAppIssueCommentId) {
        this.parentAppIssueCommentId = parentAppIssueCommentId;
    }

    public List<AppIssueComment> getChildComments() {
        if (childComments == null) {
           return null;
        }
        return childComments;
    }

    public void setChildComments(List<AppIssueComment> childComments) {
        this.childComments = childComments;
    }

    public List<User> getTaggedUserList() {
        return taggedUserList;
    }

    public void setTaggedUserList(List<User> taggedUserList) {
        this.taggedUserList = taggedUserList;
    }

    @JsonIgnore
    public int getChildCommentIndex(ObjectId appIssueCommentId) {
        if (this.getChildComments() == null) {
            return -1;
        }
        for (int index = 0; index < this.getChildComments().size(); index++) {
            if (this.getChildComments().get(index).getId().equals(appIssueCommentId.toString())) {
                return index;
            }
        }
        return -1;
    }
}
