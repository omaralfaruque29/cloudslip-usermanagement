package com.cloudslip.usermanagement.dto.VpcGroup;

import com.cloudslip.usermanagement.dto.BaseInputDTO;
import org.bson.types.ObjectId;

import java.util.List;

public class CreateVpcGroupDTO extends BaseInputDTO {
    private ObjectId companyId;
    private String  name;
    private List<ObjectId> vpcIdList;

    public CreateVpcGroupDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ObjectId getCompanyId() {
        return companyId;
    }

    public void setCompanyId(ObjectId companyId) {
        this.companyId = companyId;
    }

    public List<ObjectId> getVpcIdList() {
        return vpcIdList;
    }

    public void setVpcIdList(List<ObjectId> vpcIdList) {
        this.vpcIdList = vpcIdList;
    }
}
