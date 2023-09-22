package com.cloudslip.usermanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "vpc_group")
public class VpcGroup extends BaseEntity {

    private ObjectId companyId;
    private String name;
    private List<Vpc> vpcList;

    public VpcGroup() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Vpc> getVpcList() {
        return vpcList;
    }

    public void setVpcList(List<Vpc> vpcList) {
        this.vpcList = vpcList;
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
}
