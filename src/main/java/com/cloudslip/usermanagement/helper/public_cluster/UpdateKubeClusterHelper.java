package com.cloudslip.usermanagement.helper.public_cluster;

import com.cloudslip.usermanagement.dto.*;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.enums.Status;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.*;
import com.cloudslip.usermanagement.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UpdateKubeClusterHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(UpdateKubeClusterHelper.class);

    private UpdateKubeClusterInputDTO input;
    private ResponseDTO output = new ResponseDTO();

    @Autowired
    private KubeClusterRepository kubeClusterRepository;

    @Autowired
    private RegionRepository regionRepository;

    private Optional<Region> region;
    private Optional<KubeCluster> kubeCluster;

    @Autowired
    private ObjectMapper objectMapper;

    public void init(BaseInput input, Object... extraParams) {
        this.input = (UpdateKubeClusterInputDTO) input;
        this.setOutput(output);
        kubeCluster = null;
    }

    protected void checkPermission() {
        if (requester == null || !requester.hasAnyAuthority(Authority.ROLE_SUPER_ADMIN)) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(output.getMessage(), this.getClass().getName());
        }
    }

    protected void checkValidity() {
        if(!input.checkRequiredValidity()) {
            output.generateErrorResponse("Missing required field!");
            throw new ApiErrorException(output.getMessage(), this.getClass().getName());
        }

        if(input.getTotalCPU() < input.getAvailableCPU()) {
            output.generateErrorResponse("Available CPU cannot be bigger than Total CPU");
            throw new ApiErrorException(output.getMessage(), this.getClass().getName());
        }

        if(input.getTotalMemory() < input.getAvailableMemory()) {
            output.generateErrorResponse("Available Memory cannot be bigger than Total Memory");
            throw new ApiErrorException(output.getMessage(), this.getClass().getName());
        }

        if(input.getTotalStorage() < input.getAvailableStorage()) {
            output.generateErrorResponse("Available Storage cannot be bigger than Total Storage");
            throw new ApiErrorException(output.getMessage(), this.getClass().getName());
        }

        kubeCluster = kubeClusterRepository.findByIdAndStatus(input.getId(), Status.V);
        if (!kubeCluster.isPresent()) {
            output.generateErrorResponse("Public Vpc not found!");
            throw new ApiErrorException(output.getMessage(), this.getClass().getName());
        }
    }

    protected void doPerform() {
        kubeCluster.get().setEnabled(input.isEnabled());

        if(input.getRegionId() != null) {
            region = regionRepository.findById(input.getRegionId());
            if(region.isPresent()) {
                kubeCluster.get().setRegion(region.get());
            }
        }

        kubeCluster.get().setTotalCPU(input.getTotalCPU());
        kubeCluster.get().setTotalMemory(input.getAvailableMemory());
        kubeCluster.get().setTotalStorage(input.getTotalStorage());
        kubeCluster.get().setAvailableCPU(input.getAvailableCPU());
        kubeCluster.get().setAvailableMemory(input.getAvailableMemory());
        kubeCluster.get().setAvailableStorage(input.getAvailableStorage());

        kubeCluster.get().setUpdatedBy(requester.getUsername());
        kubeCluster.get().setUpdateDate(String.valueOf(LocalDateTime.now()));
        kubeCluster.get().setLastUpdateActionId(actionId);
        kubeClusterRepository.save(kubeCluster.get());

        output.generateSuccessResponse(kubeCluster.get(), String.format("Public Vpc '%s' successfully has been updated", kubeCluster.get().getName()));
    }

    protected void postPerformCheck() {
    }

    protected void doRollback() {
    }
}
