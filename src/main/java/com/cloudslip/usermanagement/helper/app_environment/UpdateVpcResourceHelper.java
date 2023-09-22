package com.cloudslip.usermanagement.helper.app_environment;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.dto.vpcresourceupdate.VpcResourceDTO;
import com.cloudslip.usermanagement.dto.vpcresourceupdate.VpcResourceUpdateDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.enums.Status;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.Vpc;
import com.cloudslip.usermanagement.repository.VpcRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UpdateVpcResourceHelper extends AbstractHelper {

    @Autowired
    VpcRepository vpcRepository;

    private VpcResourceUpdateDTO input;
    private ResponseDTO output = new ResponseDTO();

    private List<ObjectId> vpcIdListForResponse;

    public void init(BaseInput input, Object... extraParams) {
        this.input = (VpcResourceUpdateDTO) input;
        this.setOutput(output);
        vpcIdListForResponse = new ArrayList<>();
    }


    protected void checkPermission() {
        if (requester == null || requester.hasAuthority(Authority.ANONYMOUS)) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void checkValidity() {
    }


    protected void doPerform() {
        if (input.getSelectedEnvInfoList() != null) {
            for (int envCount = 0; envCount < input.getSelectedEnvInfoList().size(); envCount++) {
                this.updateSelectedVpc(input.getSelectedEnvInfoList().get(envCount).getVpcList());
            }
        }

        if (input.getUnselectedEnvInfoList() != null) {
            for (int envCount = 0; envCount < input.getUnselectedEnvInfoList().size(); envCount++) {
                this.updateUnselectedVpc(input.getUnselectedEnvInfoList().get(envCount).getVpcList());
            }
        }

        output.generateSuccessResponse(this.getAllSelectedVpcWithUpdatedResourcesList(), "Vpc Updated for selected and unselected environments");
    }

    protected void postPerformCheck() {
    }

    protected void doRollback() {

    }


    private void updateSelectedVpc(List<VpcResourceDTO> vpcResourceDTOList) {
        for (VpcResourceDTO vpcResourceDTO : vpcResourceDTOList) {
            if (vpcResourceDTO.getVpcId() != null) {
                Optional<Vpc> vpc = vpcRepository.findByIdAndStatus(vpcResourceDTO.getVpcId(), Status.V);
                if (vpc.isPresent()) {
                    int availableCpu = vpc.get().getAvailableCPU() + (vpcResourceDTO.getPreviousSelectedCpuSize() * vpcResourceDTO.getPreviousNumberOfInstance());
                    int availableMemory = vpc.get().getAvailableMemory() + (vpcResourceDTO.getPreviousSelectedMemorySize() * vpcResourceDTO.getPreviousNumberOfInstance());
                    int availableStorage = vpc.get().getAvailableStorage() + (vpcResourceDTO.getPreviousSelectedStorageSize() * vpcResourceDTO.getPreviousNumberOfInstance());

                    vpc.get().setAvailableCPU(availableCpu - (vpcResourceDTO.getSelectedCpuSize() * vpcResourceDTO.getNumberOfInstance()));
                    vpc.get().setAvailableMemory(availableMemory - (vpcResourceDTO.getSelectedMemorySize() * vpcResourceDTO.getNumberOfInstance()));
                    vpc.get().setAvailableStorage(availableStorage - (vpcResourceDTO.getSelectedStorageSize() * vpcResourceDTO.getNumberOfInstance()));

                    vpc.get().setUpdatedBy(requester.getUsername());
                    vpc.get().setUpdateDate(String.valueOf(LocalDateTime.now()));
                    vpc.get().setLastUpdateActionId(actionId);
                    Vpc savedVpc = vpcRepository.save(vpc.get());
                    if (!vpcIdListForResponse.contains(savedVpc.getObjectId())) {
                        vpcIdListForResponse.add(savedVpc.getObjectId());
                    }
                }
            }
        }
    }


    private void updateUnselectedVpc(List<VpcResourceDTO> vpcResourceDTOList) {
        for (VpcResourceDTO vpcResourceDTO : vpcResourceDTOList) {
            if (vpcResourceDTO.getVpcId() != null) {
                Optional<Vpc> vpc = vpcRepository.findByIdAndStatus(vpcResourceDTO.getVpcId(), Status.V);
                if (vpc.isPresent()) {
                    vpc.get().setAvailableCPU(vpc.get().getAvailableCPU() + (vpcResourceDTO.getSelectedCpuSize() * vpcResourceDTO.getNumberOfInstance()));
                    vpc.get().setAvailableMemory(vpc.get().getAvailableMemory() + (vpcResourceDTO.getSelectedMemorySize() * vpcResourceDTO.getNumberOfInstance()));
                    vpc.get().setAvailableStorage(vpc.get().getAvailableStorage() + (vpcResourceDTO.getSelectedStorageSize() * vpcResourceDTO.getNumberOfInstance()));

                    vpc.get().setUpdatedBy(requester.getUsername());
                    vpc.get().setUpdateDate(String.valueOf(LocalDateTime.now()));
                    vpc.get().setLastUpdateActionId(actionId);
                    Vpc savedVpc = vpcRepository.save(vpc.get());
                    if (!vpcIdListForResponse.contains(savedVpc.getObjectId())) {
                        vpcIdListForResponse.add(savedVpc.getObjectId());
                    }
                }
            }
        }
    }

    /*
        It will get all the updated Vpc list from database for passing that to pipeline service
     */
    private List<Vpc> getAllSelectedVpcWithUpdatedResourcesList() {
        List<Vpc> vpcList = new ArrayList<>();
        for (ObjectId vpcId : vpcIdListForResponse) {
            Optional<Vpc> vpc = vpcRepository.findByIdAndStatus(vpcId, Status.V);
            if (vpc.isPresent()) {
                vpcList.add(vpc.get());
            }
        }

        return vpcList;
    }
}
