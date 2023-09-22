package com.cloudslip.usermanagement.dto.VpcGroup;

import com.cloudslip.usermanagement.dto.BaseInputDTO;
import org.bson.types.ObjectId;

import java.util.List;

public class UpdateVpcGroupDTO extends BaseInputDTO {
    private ObjectId vpcGroupId;
    private String  name;
    private List<ObjectId> vpcIdList;

    public UpdateVpcGroupDTO() {
    }

    public ObjectId getVpcGroupId() {
        return vpcGroupId;
    }

    public void setVpcGroupId(ObjectId vpcGroupId) {
        this.vpcGroupId = vpcGroupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ObjectId> getVpcIdList() {
        return vpcIdList;
    }

    public void setVpcIdList(List<ObjectId> vpcIdList) {
        this.vpcIdList = vpcIdList;
    }
}
