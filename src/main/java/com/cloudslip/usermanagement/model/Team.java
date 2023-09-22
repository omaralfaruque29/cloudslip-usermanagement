package com.cloudslip.usermanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bson.types.ObjectId;

import java.util.List;

public class Team extends BaseEntity {

    private String name;
    private String description;
    private ObjectId companyId;
    private Organization organization;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompanyId() {
        return companyId.toHexString();
    }

    @JsonIgnore
    public ObjectId getCompanyObjectId() {
        return companyId;
    }

    public void setCompanyId(ObjectId companyId) {
        this.companyId = companyId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    @JsonIgnore
    public boolean hasOrganization(List<Organization> organizationList) {
        for(Organization organization: organizationList) {
            if(this.getOrganization().getObjectId().toString().equals(organization.getObjectId().toString())) {
                return true;
            }
        }
        return false;
    }

    @JsonIgnore
    public boolean existInTeamIdList(List<ObjectId> teamIdList) {
        for (ObjectId teamId : teamIdList) {
            if (this.getObjectId().toString().equals(teamId.toString())) {
                return true;
            }
        }
        return false;
    }
}
