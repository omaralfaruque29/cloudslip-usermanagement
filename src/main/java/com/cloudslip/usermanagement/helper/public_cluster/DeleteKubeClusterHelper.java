package com.cloudslip.usermanagement.helper.public_cluster;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.GetObjectInputDTO;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.enums.Status;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.kafka.KafkaPublisher;
import com.cloudslip.usermanagement.model.KubeCluster;
import com.cloudslip.usermanagement.repository.KubeClusterRepository;
import com.cloudslip.usermanagement.repository.EnvironmentOptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class DeleteKubeClusterHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(DeleteKubeClusterHelper.class);

    private GetObjectInputDTO input;
    private ResponseDTO output = new ResponseDTO();

    @Autowired
    KubeClusterRepository kubeClusterRepository;

    @Autowired
    EnvironmentOptionRepository environmentOptionRepository;

    @Autowired
    private KafkaPublisher kafkaPublisher;

    private Optional<KubeCluster> kubeCluster;

    public void init(BaseInput input, Object... extraParams) {
        this.input = (GetObjectInputDTO) input;
        this.setOutput(output);
    }


    protected void checkPermission() {
        if (requester == null || !requester.hasAnyAuthority(Authority.ROLE_SUPER_ADMIN)) {
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


    protected void doPerform() throws ApiErrorException {
        kubeCluster.get().setUpdatedBy(requester.getUsername());
        kubeCluster.get().setUpdateDate(String.valueOf(LocalDateTime.now()));
        kubeCluster.get().setLastUpdateActionId(actionId);
        kubeCluster.get().setStatus(Status.D);
        kubeCluster.get().setEnabled(false);
        kubeClusterRepository.save(kubeCluster.get());

        kafkaPublisher.deleteTopic(kubeCluster.get().getKafkaTopic());
        //TODO: Delete Vpc Agent Service User from Listener Service

        output.generateSuccessResponse(null, "Public Vpc successfully has been deleted");
    }


    protected void postPerformCheck() {
    }

    protected void doRollback() {
    }
}
