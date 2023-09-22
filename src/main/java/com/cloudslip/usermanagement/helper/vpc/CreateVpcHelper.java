package com.cloudslip.usermanagement.helper.vpc;

import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.dto.SaveVpcDTO;
import com.cloudslip.usermanagement.dto.kubeconfig.NamespaceConfig;
import com.cloudslip.usermanagement.dto.kubeconfig.SecretConfig;
import com.cloudslip.usermanagement.enums.AgentCommand;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.enums.Status;
import com.cloudslip.usermanagement.enums.VpcStatus;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.kafka.KafkaPublisher;
import com.cloudslip.usermanagement.kafka.dto.KafkaMessage;
import com.cloudslip.usermanagement.kafka.dto.KafkaMessageHeader;
import com.cloudslip.usermanagement.model.*;
import com.cloudslip.usermanagement.repository.*;
import com.cloudslip.usermanagement.util.Utils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CreateVpcHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(CreateVpcHelper.class);

    private SaveVpcDTO input;
    private ResponseDTO output = new ResponseDTO();
    private Optional<Vpc> latestVpc;
    private Optional<Company> company;
    private Optional<Region> region;
    private Vpc newVpc;
    private KubeCluster selectedKubeCluster;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private KubeClusterRepository kubeClusterRepository;

    @Autowired
    private VpcRepository vpcRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private KafkaPublisher kafkaPublisher;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private TeamRepository teamRepository;


    public void init(BaseInput input, Object... extraParams) {
        this.input = (SaveVpcDTO) input;
        this.setOutput(output);
    }


    protected void checkPermission() {
        if (requester == null || !requester.hasAnyAuthority(Authority.ROLE_SUPER_ADMIN, Authority.ROLE_ADMIN)) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void checkValidity() {
        if (input.getName() == null || input.getName().equals("")) {
            output.generateErrorResponse("Name is required!");
            throw new ApiErrorException(this.getClass().getName());
        }
        this.input.setName(this.input.getName().trim().replaceAll(" +", " "));

        region = regionRepository.findByIdAndStatus(input.getRegionId(), Status.V);
        if(!region.isPresent()) {
            output.generateErrorResponse("Region not found!");
            throw new ApiErrorException(output.getMessage(), this.getClass().getName());
        }

        selectedKubeCluster = findASuitableKubeCluster();
        if(selectedKubeCluster == null) {
            output.generateErrorResponse("No Public Vpc found in the desired region");
            throw new ApiErrorException(output.getMessage(), this.getClass().getName());
        }

        ObjectId companyId = null;
        if (requester.hasAuthority(Authority.ROLE_SUPER_ADMIN)) {
            if (input.getCompanyId() == null) {
                output.generateErrorResponse("Company Id is required!");
                throw new ApiErrorException(output.getMessage(), this.getClass().getName());
            }
            companyId = input.getCompanyId();
        } else {
            companyId = requester.getCompanyId();
        }
        company = companyRepository.findById(companyId);
        if(!company.isPresent()) {
            output.generateErrorResponse("Company not found!");
            throw new ApiErrorException(output.getMessage(), this.getClass().getName());
        }

        Optional<Vpc> vpc = vpcRepository.findByNameIgnoreCaseAndCompanyIdAndStatus(input.getName(), company.get().getObjectId(), Status.V);
        if (vpc.isPresent()) {
            output.generateErrorResponse("Duplicate Name!");
            throw new ApiErrorException(output.getMessage(), this.getClass().getName());
        }
    }

    private KubeCluster findASuitableKubeCluster() {
        List<KubeCluster> kubeClusterList = kubeClusterRepository.findAllByRegion_IdAndStatus(input.getRegionId(), Status.V);
        if(kubeClusterList.size() == 0) {
            return null;
        }
        KubeCluster selectedKubeCluster = null;
        for(KubeCluster kubeCluster : kubeClusterList) {
            if(kubeCluster.hasEnoughResource(input.getTotalCPU(), input.getTotalMemory(), input.getTotalStorage())) {
                if(selectedKubeCluster == null) {
                    selectedKubeCluster = kubeCluster;
                } else if(!kubeCluster.hasMoreResourceAvailableThan(selectedKubeCluster))  {
                    selectedKubeCluster = kubeCluster;
                }
            }
        }
        if(selectedKubeCluster == null) {
            output.generateErrorResponse("Not enough resource available in your desired region!");
            throw new ApiErrorException(output.getMessage(), this.getClass().getName());
        }
        return selectedKubeCluster;
    }

    protected void doPerform() {
        newVpc = new Vpc();
        newVpc.setId(new ObjectId());
        newVpc.setKubeCluster(selectedKubeCluster);
        newVpc.setName(input.getName());
        newVpc.setNamespace(generateNamespace(input.getName(), newVpc.getId()));
        newVpc.setBandwidth(input.getBandwidth());
        newVpc.setCompanyId(requester.getCompanyId());
        newVpc.setVpcStatus(VpcStatus.INITIALIZING);

        newVpc.setTotalCPU(input.getTotalCPU());
        newVpc.setTotalMemory(input.getTotalMemory());
        newVpc.setTotalStorage(input.getTotalStorage());
        newVpc.setAvailableCPU(input.getTotalCPU());
        newVpc.setAvailableMemory(input.getTotalMemory());
        newVpc.setAvailableStorage(input.getTotalStorage());

        newVpc.setCreatedBy(requester.getUsername());
        newVpc.setCreateDate(String.valueOf(LocalDateTime.now()));
        newVpc.setCreateActionId(actionId);

        if(input.isAutoScalingEnabled()){
            newVpc.setExtendedCPU(input.getTotalCPU() + (int)(input.getTotalCPU() * 0.25));
            newVpc.setExtendedMemory(input.getTotalMemory() + (int)(input.getTotalMemory() * 0.25));
            newVpc.setExtendedStorage(input.getTotalStorage() + (int)(input.getTotalStorage() * 0.25));
            newVpc.setAutoScalingEnabled(true);
        } else {
            newVpc.setExtendedCPU(input.getTotalCPU());
            newVpc.setExtendedMemory(input.getTotalMemory());
            newVpc.setExtendedStorage(input.getTotalStorage());
            newVpc.setAutoScalingEnabled(false);
        }

        if(input.getOrganizationId() != null){
            Organization organization = organizationRepository.findById(input.getOrganizationId()).get();
            if(organization != null){
                newVpc.setOrganization(organization);
            }
        }

        if(input.getTeamIdList() != null && input.getTeamIdList().size() > 0){
            List<Team> teamList = new ArrayList();
            for(ObjectId objectId : input.getTeamIdList()){
                Team team = teamRepository.findById(objectId).get();
                if(team != null){
                    teamList.add(team);
                }
            }
            newVpc.setTeamlist((List<Team>) teamList);
        }

        publishMessageInKafkaToCreateNamespace(this.newVpc.getId(), this.newVpc.getNamespace(), this.newVpc.getExtendedCPU(),
                this.newVpc.getExtendedMemory(), this.newVpc.getExtendedStorage(), this.newVpc.getCompanyId(), this.newVpc.getKubeCluster().getKafkaTopic());

        newVpc = vpcRepository.save(newVpc);

        //Deduct select resources from available resources in the selected Vpc
        selectedKubeCluster.deductFromAvailableResource(newVpc.getExtendedCPU(), newVpc.getExtendedMemory(), newVpc.getExtendedStorage());
        kubeClusterRepository.save(selectedKubeCluster);

        generateDockerHubSecretConfigForNewVpcAndPublishInKafka(newVpc);

        output.generateSuccessResponse(newVpc, String.format("Vpc '%s' successfully has been created", input.getName()));
    }

    private void publishMessageInKafkaToCreateNamespace(String vpcId, String namespace, int totalCpu, int totalMemory, int totalStorage, String companyId, String targetVpcKafkaTopic) {
        log.info("Publishing Kafka Message for Namespace creation");
        KafkaMessageHeader header = new KafkaMessageHeader(namespace, AgentCommand.CREATE_NAMESPACE.toString());
        header.setCompanyId(companyId);
        header.addToExtra("vpcId", vpcId);
        NamespaceConfig body = new NamespaceConfig(namespace, totalCpu, totalMemory, totalStorage);
        KafkaMessage<NamespaceConfig> message = new KafkaMessage(header, body);
        this.kafkaPublisher.publishMessage(targetVpcKafkaTopic, message);
    }

    private void generateDockerHubSecretConfigForNewVpcAndPublishInKafka(Vpc newVpc) {
        if(company.get().getDockerHubInfo() != null) {
            String secretName = "regcred";
            SecretConfig secretConfig = new SecretConfig();
            secretConfig.generateForDockerhubCredentials(secretName, newVpc.getNamespace(), company.get().getDockerHubInfo().getDockerConfigData());
            publishKafkaMessageForCreatingDockerHubSecret(newVpc.getId(), newVpc.getKubeCluster().getKafkaTopic(), secretConfig);
        }
    }

    private void publishKafkaMessageForCreatingDockerHubSecret(String vpcId, String targetVpcKafkaTopic, SecretConfig secretConfig) {
        KafkaMessageHeader header = new KafkaMessageHeader(secretConfig.getNamespace(), AgentCommand.CREATE_SECRET.toString());
        header.setCompanyId(company.get().getId());
        header.addToExtra("vpcId", vpcId);
        KafkaMessage<SecretConfig> message = new KafkaMessage(header, secretConfig);
        this.kafkaPublisher.publishMessage(targetVpcKafkaTopic, message);
    }


    protected void postPerformCheck() {
    }

    protected void doRollback() {

    }

    private String generateNamespace(String vpcName, String vpcId) {
        return (Utils.removeAllSpaceWithDash(company.get().getName()) + "-" + Utils.removeAllSpaceWithDash(vpcName)).toLowerCase() + "-" + vpcId.toLowerCase();
    }
}
