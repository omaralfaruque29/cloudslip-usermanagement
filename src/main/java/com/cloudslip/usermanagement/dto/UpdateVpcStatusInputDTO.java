package com.cloudslip.usermanagement.dto;

import com.cloudslip.usermanagement.enums.VpcStatus;
import org.bson.types.ObjectId;

import javax.validation.constraints.NotNull;

public class UpdateVpcStatusInputDTO extends BaseInputDTO {

    @NotNull
    private ObjectId id;

    private VpcStatus vpcStatus;

    public UpdateVpcStatusInputDTO() {
    }

    public UpdateVpcStatusInputDTO(@NotNull ObjectId id, VpcStatus vpcStatus) {
        this.id = id;
        this.vpcStatus = vpcStatus;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public VpcStatus getVpcStatus() {
        return vpcStatus;
    }

    public void setVpcStatus(VpcStatus vpcStatus) {
        this.vpcStatus = vpcStatus;
    }
}
