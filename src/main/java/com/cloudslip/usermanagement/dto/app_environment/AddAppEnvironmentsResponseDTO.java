package com.cloudslip.usermanagement.dto.app_environment;

import com.cloudslip.usermanagement.dto.app_environment.VpcResourceDetails;
import com.cloudslip.usermanagement.model.*;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;

public class AddAppEnvironmentsResponseDTO {

    private Company company;
    private List<EnvironmentOption> environmentOptionList;
    private Map<ObjectId, List<VpcResourceDetails>> vpcResourceDetailsMap;

    public AddAppEnvironmentsResponseDTO() {
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public List<EnvironmentOption> getEnvironmentOptionList() {
        return environmentOptionList;
    }

    public void setEnvironmentOptionList(List<EnvironmentOption> environmentOptionList) {
        this.environmentOptionList = environmentOptionList;
    }

    public Map<ObjectId, List<VpcResourceDetails>> getVpcResourceDetailsMap() {
        return vpcResourceDetailsMap;
    }

    public void setVpcResourceDetailsMap(Map<ObjectId, List<VpcResourceDetails>> vpcResourceDetailsMap) {
        this.vpcResourceDetailsMap = vpcResourceDetailsMap;
    }
}
