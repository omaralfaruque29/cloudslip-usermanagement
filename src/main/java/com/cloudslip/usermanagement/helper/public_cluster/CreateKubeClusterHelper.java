package com.cloudslip.usermanagement.helper.public_cluster;

import com.cloudslip.usermanagement.constant.ApplicationProperties;
import com.cloudslip.usermanagement.dto.*;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.enums.ResponseStatus;
import com.cloudslip.usermanagement.enums.Status;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.kafka.KafkaPublisher;
import com.cloudslip.usermanagement.model.*;
import com.cloudslip.usermanagement.repository.KubeClusterRepository;
import com.cloudslip.usermanagement.repository.RegionRepository;
import com.cloudslip.usermanagement.util.Utils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CreateKubeClusterHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(CreateKubeClusterHelper.class);

    private CreateKubeClusterInputDTO input;
    private ResponseDTO output = new ResponseDTO();

    @Autowired
    private KubeClusterRepository kubeClusterRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private KafkaPublisher kafkaPublisher;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private Environment env;

    private KubeCluster newKubeCluster;


    private String kafkaTopic = "";

    public void init(BaseInput input, Object... extraParams) {
        this.input = (CreateKubeClusterInputDTO) input;
        this.setOutput(output);
    }


    protected void checkPermission() {
        if(requester == null || !requester.hasAnyAuthority(Authority.ROLE_SUPER_ADMIN)) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void checkValidity() {
        Optional<KubeCluster> kubeCluster = kubeClusterRepository.findByNameIgnoreCaseAndStatus(input.getName(), Status.V);
        if (kubeCluster.isPresent()) {
            output.generateErrorResponse("Duplicate Vpc Name!");
            throw new ApiErrorException(output.getMessage(), this.getClass().getName());
        }
        
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
    }


    protected void doPerform() {
        newKubeCluster = new KubeCluster();

        kafkaTopic = generateTopicNameForCluster(input.getName());

        if(env.getActiveProfiles().length > 0 && Utils.isStringEquals(env.getActiveProfiles()[0], "prod")) {
            kafkaPublisher.createTopic(kafkaTopic);
            try {
                //Create a User in Listener Service for a Vpc Agent
                CreateClusterAgentServiceUserDTO createClusterAgentServiceUserInput = new CreateClusterAgentServiceUserDTO();
                createClusterAgentServiceUserInput.setKafkaTopic(kafkaTopic);
                createClusterAgentServiceUserInput.setUsername(generateUserNameForClusterAgentService(input.getName()));
                HttpEntity<CreateClusterAgentServiceUserDTO> request = new HttpEntity<>(createClusterAgentServiceUserInput);
                ResponseDTO response = restTemplate.postForObject(applicationProperties.getListenerServiceBaseUrl() + "api/user/create-for-cluster-agent-service?accessToken=" + applicationProperties.getListenerServiceApiAccessToken(), request, ResponseDTO.class);
                if (response.getStatus().equals(ResponseStatus.error)) {
                    output.generateErrorResponse(response.getMessage());
                    throw new ApiErrorException(this.getClass().getName());
                }
            } catch (ResourceAccessException ex) {
                output.generateErrorResponse(ex.getMessage());
                throw new ApiErrorException(this.getClass().getName(), true);
            } catch (HttpClientErrorException ex) {
                output.generateErrorResponse(ex.getMessage());
                throw new ApiErrorException(this.getClass().getName(), true);
            } catch (Exception ex) {
                output.generateErrorResponse(ex.getMessage());
                throw new ApiErrorException(this.getClass().getName(), true);
            }
        }

        newKubeCluster.setId(new ObjectId());
        newKubeCluster.setKafkaTopic(kafkaTopic);
        newKubeCluster.setName(input.getName());
        newKubeCluster.setDashboardUrl(input.getDashboardUrl());
        newKubeCluster.setDefaultNamespace(input.getDefaultNamespace());
        newKubeCluster.setEnabled(true);

        newKubeCluster.setTotalCPU(input.getTotalCPU());
        newKubeCluster.setTotalMemory(input.getAvailableMemory());
        newKubeCluster.setTotalStorage(input.getTotalStorage());
        newKubeCluster.setAvailableCPU(input.getAvailableCPU());
        newKubeCluster.setAvailableMemory(input.getAvailableMemory());
        newKubeCluster.setAvailableStorage(input.getAvailableStorage());

        if(input.getRegionId() != null) {
            Optional<Region> region = regionRepository.findById(input.getRegionId());
            if(region.isPresent()) {
                newKubeCluster.setRegion(region.get());
            }
        }

        newKubeCluster.setCreatedBy(requester.getUsername());
        newKubeCluster.setCreateDate(String.valueOf(LocalDateTime.now()));
        newKubeCluster.setCreateActionId(actionId);

        newKubeCluster = kubeClusterRepository.save(newKubeCluster);
        output.generateSuccessResponse(newKubeCluster, String.format(" Public Vpc '%s' successfully has been created", input.getName()));
    }


    protected void postPerformCheck() {
    }

    protected void doRollback() {
        kafkaPublisher.deleteTopic(kafkaTopic);
    }

    private String generateTopicNameForCluster(String clusterName) {
        return ("kt-cloudslip-public" + "-" + Utils.removeAllSpaceWithUnderScore(clusterName)).toLowerCase();
    }

    private String generateUserNameForClusterAgentService(String clusterName) {
        return ("ca-cloudslip-public" + "-" + Utils.removeAllSpaceWithUnderScore(clusterName)).toLowerCase();
    }

}
