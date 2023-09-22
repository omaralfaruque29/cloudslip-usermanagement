package com.cloudslip.usermanagement.helper.vpc;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.dto.UpdateVpcDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.enums.Status;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.Vpc;
import com.cloudslip.usermanagement.model.Region;
import com.cloudslip.usermanagement.repository.VpcRepository;
import com.cloudslip.usermanagement.repository.RegionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UpdateVpcHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(UpdateVpcHelper.class);

    private UpdateVpcDTO input;

    private ResponseDTO output = new ResponseDTO();

    @Autowired
    private VpcRepository vpcRepository;


    private Optional<Vpc> vpc;

    public void init(BaseInput input, Object... extraParams) {
        this.input = (UpdateVpcDTO) input;
        this.setOutput(output);
        vpc = null;
    }


    protected void checkPermission() {
        if (requester == null || !requester.hasAnyAuthority(Authority.ROLE_SUPER_ADMIN, Authority.ROLE_ADMIN)) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void checkValidity() {
        vpc = vpcRepository.findByIdAndStatus(input.getId(), Status.V);
        if (!vpc.isPresent()) {
            output.generateErrorResponse("Vpc not found!");
            throw new ApiErrorException(this.getClass().getName());
        } else if (input.getName() == null || input.getName().equals("")) {
            output.generateErrorResponse("Invalid Vpc Name!");
            throw new ApiErrorException(this.getClass().getName());
        }
        this.input.setName(this.input.getName().trim().replaceAll(" +", " "));

        Optional<Vpc> existingVpc = vpcRepository.findByNameIgnoreCaseAndCompanyIdAndStatusAndIdNotIn(input.getName(),
                vpc.get().getCompanyObjectId(), Status.V, vpc.get().getObjectId());
        if (existingVpc.isPresent()) {
            output.generateErrorResponse("Duplicate Vpc Name!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void doPerform() {
        Map<String, Vpc> map = new HashMap<>();
        Vpc oldVpc = vpc.get();
        map.put("oldVpc", oldVpc);
        Vpc updatedVpc = new Vpc();
        updatedVpc.setId(vpc.get().getObjectId());
        updatedVpc.setStatus(vpc.get().getStatus());
        updatedVpc.setCreatedBy(vpc.get().getCreatedBy());
        updatedVpc.setCreateDate(vpc.get().getCreateDate());
        updatedVpc.setOrderNo(vpc.get().getOrderNo());
        updatedVpc.setCreateActionId(vpc.get().getCreateActionId());
        updatedVpc.setName(input.getName());
        updatedVpc.setBandwidth(input.getBandwidth() != null ? input.getBandwidth() : oldVpc.getBandwidth());
        updatedVpc.setDashboardUrl(input.getDashboardUrl());
        updatedVpc.setTotalCPU(input.getTotalCPU());
        updatedVpc.setAvailableCPU(vpc.get().getAvailableCPU() + (input.getTotalCPU() - vpc.get().getTotalCPU()));
        updatedVpc.setTotalMemory(input.getTotalMemory());
        updatedVpc.setAvailableMemory(vpc.get().getAvailableMemory() + (input.getTotalMemory() - vpc.get().getTotalMemory()));
        updatedVpc.setTotalStorage(input.getTotalStorage());
        updatedVpc.setAvailableStorage(vpc.get().getAvailableStorage() + (input.getTotalStorage() - vpc.get().getTotalStorage()));
        updatedVpc.setUpdatedBy(requester.getUsername());
        updatedVpc.setUpdateDate(String.valueOf(LocalDateTime.now()));
        updatedVpc.setLastUpdateActionId(actionId);
        if(input.isAutoScalingEnabled()){
            updatedVpc.setExtendedCPU(input.getTotalCPU() + (int)(input.getTotalCPU() * 0.25));
            updatedVpc.setExtendedStorage(input.getTotalStorage() + (int)(input.getTotalStorage() * 0.25));
            updatedVpc.setExtendedMemory(input.getTotalMemory() + (int)(input.getTotalMemory() * 0.25));
            updatedVpc.setAutoScalingEnabled(true);
        } else {
            updatedVpc.setExtendedCPU(input.getTotalCPU());
            updatedVpc.setExtendedStorage(input.getTotalStorage());
            updatedVpc.setExtendedMemory(input.getTotalMemory());
            updatedVpc.setAutoScalingEnabled(false);
        }

        map.put("updatedVpc", updatedVpc);
        vpcRepository.save(updatedVpc);
        output.generateSuccessResponse(map, String.format("Vpc '%s' successfully has been updated", input.getName()));
    }



    protected void postPerformCheck() {
    }

    protected void doRollback() {

    }

}
