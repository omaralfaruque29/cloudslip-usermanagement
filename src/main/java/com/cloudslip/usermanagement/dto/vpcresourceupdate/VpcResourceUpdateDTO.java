package com.cloudslip.usermanagement.dto.vpcresourceupdate;

import com.cloudslip.usermanagement.dto.BaseInputDTO;

import java.util.List;

public class VpcResourceUpdateDTO extends BaseInputDTO {
    private List<EnvironmentInfoUpdateDTO> selectedEnvInfoList;
    private List<EnvironmentInfoUpdateDTO> unselectedEnvInfoList;

    public List<EnvironmentInfoUpdateDTO> getSelectedEnvInfoList() {
        return selectedEnvInfoList;
    }

    public void setSelectedEnvInfoList(List<EnvironmentInfoUpdateDTO> selectedEnvInfoList) {
        this.selectedEnvInfoList = selectedEnvInfoList;
    }

    public List<EnvironmentInfoUpdateDTO> getUnselectedEnvInfoList() {
        return unselectedEnvInfoList;
    }

    public void setUnselectedEnvInfoList(List<EnvironmentInfoUpdateDTO> unselectedEnvInfoList) {
        this.unselectedEnvInfoList = unselectedEnvInfoList;
    }
}
