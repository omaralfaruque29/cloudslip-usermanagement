package com.cloudslip.usermanagement.dto;

import org.bson.types.ObjectId;

import javax.validation.constraints.NotNull;
import java.util.List;

public class SetEnvironmentForCompanyDTO extends BaseInputDTO {

    private ObjectId companyId;

    @NotNull
    private List<ObjectId> environmentList;

    public SetEnvironmentForCompanyDTO() {
    }

    public SetEnvironmentForCompanyDTO(ObjectId companyId, List<ObjectId> environmentList) {
        this.companyId = companyId;
        this.environmentList = environmentList;
    }

    public ObjectId getCompanyId() {
        return companyId;
    }

    public void setCompanyId(ObjectId companyId) {
        this.companyId = companyId;
    }

    public List<ObjectId> getEnvironmentList() {
        return environmentList;
    }

    public void setEnvironmentList(List<ObjectId> environmentList) {
        this.environmentList = environmentList;
    }
}
