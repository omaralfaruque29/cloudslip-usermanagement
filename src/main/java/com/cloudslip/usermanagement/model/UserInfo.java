package com.cloudslip.usermanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bson.types.ObjectId;
import java.util.List;


public class UserInfo extends BaseEntity {

    private static final long serialVersionUID = 7954325925563724664L;

    private ObjectId userId;
    private String email;
    private String firstName;
    private String lastName;
    private Company company;
    private Organization organization;
    private List<Team> teams;

    public UserInfo() {

    }

    public UserInfo(ObjectId userId, String email, String firstName, String lastName, Company company, Organization organization, List<Team> teams) {
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.company = company;
        this.organization = organization;
        this.teams = teams;
    }

    public String getUserId() {
        if(userId != null) {
            return userId.toHexString();
        }
        return null;
    }

    @JsonIgnore
    public ObjectId getUserIdObject() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    @JsonIgnore
    public boolean hasTeam(ObjectId teamId) {
        List<Team> userTeams = this.getTeams();
        if (userTeams == null) {
           return false;
        }
        for (Team team: userTeams) {
            if (team.getObjectId().toString().equals(teamId.toString())) {
                return true;
            }
        }
        return false;
    }
}
