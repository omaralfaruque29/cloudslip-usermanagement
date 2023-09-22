package com.cloudslip.usermanagement.helper.public_cluster;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.GetObjectInputDTO;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.enums.Status;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.KubeCluster;
import com.cloudslip.usermanagement.repository.KubeClusterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetKubeClusterHelper extends AbstractHelper {

    private GetObjectInputDTO input;

    private ResponseDTO output = new ResponseDTO();

    @Autowired
    private KubeClusterRepository kubeClusterRepository;

    private Optional<KubeCluster> kubeCluster;

    public void init(BaseInput input, Object... extraParams) {
        this.input = (GetObjectInputDTO) input;
        this.setOutput(output);
    }

    protected void checkPermission() {
        if(requester == null || !requester.hasAnyAuthority(Authority.ROLE_SUPER_ADMIN, Authority.ROLE_ADMIN)) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }

    protected void checkValidity() {
        kubeCluster = kubeClusterRepository.findByIdAndStatus(input.getId(), Status.V);
        if(!kubeCluster.isPresent()) {
            output.generateErrorResponse("Public Vpc Not Found!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }

    protected void doPerform() {
        output.generateSuccessResponse(kubeCluster);
    }

    protected void postPerformCheck() {
    }

    protected void doRollback() {

    }
}
