package com.cloudslip.usermanagement.helper.vpc;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.GetObjectInputDTO;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.dto.kubeconfig.NamespaceConfig;
import com.cloudslip.usermanagement.enums.AgentCommand;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.enums.Status;
import com.cloudslip.usermanagement.enums.VpcStatus;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.kafka.KafkaPublisher;
import com.cloudslip.usermanagement.kafka.dto.KafkaMessage;
import com.cloudslip.usermanagement.kafka.dto.KafkaMessageHeader;
import com.cloudslip.usermanagement.model.KubeCluster;
import com.cloudslip.usermanagement.model.Team;
import com.cloudslip.usermanagement.model.Vpc;
import com.cloudslip.usermanagement.repository.KubeClusterRepository;
import com.cloudslip.usermanagement.repository.VpcRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class DeleteVpcHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(DeleteVpcHelper.class);

    @Autowired
    private VpcRepository vpcRepository;

    @Autowired
    private KubeClusterRepository kubeClusterRepository;

    @Autowired
    private KafkaPublisher kafkaPublisher;

    private GetObjectInputDTO input;
    private ResponseDTO output = new ResponseDTO();

    private Optional<Vpc> vpc;
    private Optional<KubeCluster> kubeCluster;

    public void init(BaseInput input, Object... extraParams) {
        this.input = (GetObjectInputDTO) input;
        this.setOutput(output);
    }


    protected void checkPermission() {
        if (requester == null || !requester.hasAnyAuthority(Authority.ROLE_SUPER_ADMIN, Authority.ROLE_ADMIN)) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(output.getMessage(), this.getClass().getName());
        }
    }


    protected void checkValidity() {
        vpc = vpcRepository.findByIdAndStatus(input.getId(), Status.V);
        if(!vpc.isPresent()) {
            output.generateErrorResponse("Vpc Not Found!");
            throw new ApiErrorException(output.getMessage(), this.getClass().getName());
        }

        kubeCluster = kubeClusterRepository.findByIdAndStatus(vpc.get().getKubeCluster().getObjectId(), Status.V);
        if(!kubeCluster.isPresent()) {
            output.generateErrorResponse("Kube Cluster Not Found!");
            throw new ApiErrorException(output.getMessage(), this.getClass().getName());
        }

        if(!requester.getCompanyId().toString().equals(vpc.get().getCompanyId().toString())){
            output.generateErrorResponse("Company Not Found!");
            throw new ApiErrorException(this.getClass().getName());
        }
        if (!requester.hasAuthority(Authority.ROLE_SUPER_ADMIN) &&
                !requester.hasAuthority(Authority.ROLE_ADMIN) &&
                !requester.hasAuthority(Authority.ROLE_DEV) &&
                !requester.hasAuthority(Authority.ROLE_OPS)) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }

        if(requester.getOrganizationId() != null && vpc.get().getOrganization().getObjectId() != null && !requester.getOrganizationId().equals(vpc.get().getOrganization().getObjectId())){
            output.generateErrorResponse("This vpc does not belong to requester's organization");
            throw new ApiErrorException(this.getClass().getName());
        }

        if(vpc.get().getTeamlist() != null && vpc.get().getTeamlist().size() > 0){
            if(!vpcBelongsToRequesterTeam()){
                output.generateErrorResponse("This vpc does not belong to requester's teams");
                throw new ApiErrorException(this.getClass().getName());
            }
        }
    }


    protected void doPerform() {
        vpc.get().setUpdatedBy(requester.getUsername());
        vpc.get().setUpdateDate(String.valueOf(LocalDateTime.now()));
        vpc.get().setLastUpdateActionId(actionId);
        vpc.get().setVpcStatus(VpcStatus.TERMINATING);

        vpcRepository.save(vpc.get());

        updateKubeClusterAvailableResources();

        this.removeAllApplicationsDeployedInVpc();

        this.publishKafkaMessageForDeletingNamespace();

        output.generateSuccessResponse(null, "Vpc successfully has been deleted");
    }

    private void updateKubeClusterAvailableResources() {
        this.kubeCluster.get().addToAvailableResource(vpc.get().getExtendedCPU(), vpc.get().getExtendedMemory(), vpc.get().getExtendedStorage());
        this.kubeClusterRepository.save(this.kubeCluster.get());
    }

    private void removeAllApplicationsDeployedInVpc() {
        //TODO: Find all applications which are running in this vpc and delete them all with associated data
    }

    private void publishKafkaMessageForDeletingNamespace() {
        log.info("Publishing Kafka Message for Removing Namespace: {}", vpc.get().getNamespace());
        KafkaMessageHeader header = new KafkaMessageHeader(vpc.get().getNamespace(), AgentCommand.REMOVE_NAMESPACE.toString());
        header.setCompanyId(vpc.get().getCompanyId());
        header.addToExtra("vpcId", vpc.get().getId());
        NamespaceConfig body = new NamespaceConfig(vpc.get().getNamespace());
        KafkaMessage<NamespaceConfig> message = new KafkaMessage(header, body);
        this.kafkaPublisher.publishMessage(vpc.get().getKubeCluster().getKafkaTopic(), message);
    }

    protected void postPerformCheck() {
    }

    protected void doRollback() {

    }

    private boolean vpcBelongsToRequesterTeam() {
        boolean vpcBelongsToRequesterTeam = false;
        for(Team team : vpc.get().getTeamlist()){
            try {
                if(requester.getTeamIdList().contains(team.getObjectId())){
                    vpcBelongsToRequesterTeam = true;
                    break;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return vpcBelongsToRequesterTeam;
    }
}
