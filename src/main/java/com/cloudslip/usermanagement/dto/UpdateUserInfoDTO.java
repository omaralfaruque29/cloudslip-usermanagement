package com.cloudslip.usermanagement.dto;

import com.cloudslip.usermanagement.model.Company;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.List;

public class UpdateUserInfoDTO extends BaseInputDTO  {

    private ObjectId id;
    private String firstName;
    private String lastName;
    private String email;
    private ObjectId companyId;
    private boolean isEnabled;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public ObjectId getCompanyId() {
        return companyId;
    }

    public void setCompanyId(ObjectId companyId) {
        this.companyId = companyId;
    }
}
