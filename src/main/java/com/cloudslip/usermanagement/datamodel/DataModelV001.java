package com.cloudslip.usermanagement.datamodel;


import com.cloudslip.usermanagement.dto.CreateClusterAgentServiceUserDTO;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.enums.ResponseStatus;
import com.cloudslip.usermanagement.model.KubeCluster;
import com.cloudslip.usermanagement.model.EnvironmentOption;
import com.cloudslip.usermanagement.model.Region;
import com.cloudslip.usermanagement.model.ServerSetting;
import com.cloudslip.usermanagement.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class DataModelV001 extends AbstractDataModel {

    private Logger log = LogManager.getLogger(DataModelV001.class);

    private int version = 1;

    private DataModelManager dm;

    private Region defaultRegion;

    private Environment env;

    private String developerAlias;

    private boolean kafkaForceUse = false;


    private List<List<String>> environmentOptionDataList = Arrays.asList(
            Arrays.asList("Development", "Dev"),
            Arrays.asList("Test", "Test"),
            Arrays.asList("Quality Assurance", "QA"),
            Arrays.asList("Staging", "Staging"),
            Arrays.asList("Pre-Production", "Pre-Prod"),
            Arrays.asList("Production", "Prod")
    );

    private List<String> publicClusterDataList = Arrays.asList(
            "Alpha"
    );

    public DataModelV001() {

    }

    @Override
    protected void doExecute(DataModelManager dm, int lastDataModelVersion, Environment env) {
        this.dm = dm;
        this.env = env;
        this.developerAlias = env.getProperty("developer.alias");
        this.kafkaForceUse = Boolean.valueOf(env.getProperty("cloudslip.kafka.force-use"));

        if(lastDataModelVersion < version && isListenerServiceOn()) {
            log.info("Executing DataModelV001....");
            this.createRegion();
            this.createAllEnvironmentOptions();
            this.createPublicClusters();
            log.info("Finished Executing DataModelV001....");
        }
    }

    protected void updateDataModelVersion() {
        ServerSetting dataModelSetting = dm.getDataModelSettings();
        dataModelSetting.setCurrentValue(Integer.toString(version));
        dm.setDataModelSettings(dm.getServerSettingRepository().save(dataModelSetting));
    }

    private boolean isListenerServiceOn() {
        if(kafkaForceUse || (env.getActiveProfiles().length > 0 && Utils.isStringEquals(env.getActiveProfiles()[0], "prod"))) {
            try {
                HttpHeaders headers = new HttpHeaders();
                HttpEntity<String> request = new HttpEntity<>("parameters", headers);
                dm.getRestTemplate().exchange(dm.getApplicationProperties().getListenerServiceBaseUrl(), HttpMethod.GET, request, String.class);
                return true;
            } catch (ResourceAccessException ex) {
                log.info(ex.getMessage());
                return false;
            } catch (HttpClientErrorException ex) {
                return false;
            }
        } else  {
            return true;
        }
    }

    private void createRegion() {
        Region region = new Region();
        region.setName("US West 1");
        region.setCode("us-west-1");
        region.setDescription("Chicago");
        region.setCreateDate(String.valueOf(LocalDateTime.now()));
        defaultRegion = dm.getRegionRepository().save(region);
    }

    private void createAllEnvironmentOptions() {
        int orderNo = 1;
        for (List<String> environmentOptionData : environmentOptionDataList) {
            createEnvironmentOption(environmentOptionData.get(0), environmentOptionData.get(1), orderNo);
            orderNo++;
        }
    }

    private EnvironmentOption createEnvironmentOption(String environmentName, String environmentShortName, int orderNo) {
        EnvironmentOption environmentOption = new EnvironmentOption();
        environmentOption.setName(environmentName);
        environmentOption.setShortName(environmentShortName);
        environmentOption.setOrderNo(orderNo);
        environmentOption.setCreateDate(String.valueOf(LocalDateTime.now()));
        return dm.getEnvironmentOptionRepository().save(environmentOption);
    }

    private void createPublicClusters() {
        for(String clusterName : publicClusterDataList) {
            KubeCluster kubeCluster = new KubeCluster();
            kubeCluster.setName(clusterName);
            kubeCluster.setRegion(defaultRegion);
            kubeCluster.setDefaultNamespace("default");
            kubeCluster.setKafkaTopic(generateTopicNameForCluster(clusterName));
            kubeCluster.setTotalCPU(100000);
            kubeCluster.setAvailableCPU(100000);
            kubeCluster.setTotalMemory(3200000);
            kubeCluster.setAvailableMemory(3200000);
            kubeCluster.setTotalStorage(500000000);
            kubeCluster.setAvailableStorage(500000000);
            kubeCluster.setCreateDate(String.valueOf(LocalDateTime.now()));
            dm.getKubeClusterRepository().save(kubeCluster);

            if(kafkaForceUse || (env.getActiveProfiles().length > 0 && Utils.isStringEquals(env.getActiveProfiles()[0], "prod"))) {
                dm.getKafkaPublisher().createTopic(kubeCluster.getKafkaTopic());

                try {
                    //Create a User in Listener Service for a Vpc Agent
                    CreateClusterAgentServiceUserDTO createClusterAgentServiceUserInput = new CreateClusterAgentServiceUserDTO();
                    createClusterAgentServiceUserInput.setKafkaTopic(kubeCluster.getKafkaTopic());
                    createClusterAgentServiceUserInput.setUsername(generateUserNameForClusterAgentService(clusterName));
                    HttpEntity<CreateClusterAgentServiceUserDTO> request = new HttpEntity<>(createClusterAgentServiceUserInput);
                    ResponseDTO response = dm.getRestTemplate().postForObject(dm.getApplicationProperties().getListenerServiceBaseUrl() + "api/user/create-for-cluster-agent-service?accessToken=" + dm.getApplicationProperties().getListenerServiceApiAccessToken(), request, ResponseDTO.class);
                    if (response.getStatus().equals(ResponseStatus.error)) {
                        log.error(response.getMessage());
                    }
                } catch (Exception ex) {
                    log.error(ex.getMessage());
                }
            }
        }
    }


    private String generateTopicNameForCluster(String clusterName) {
        return ("kt-" + this.developerAlias + "-public-" + Utils.removeAllSpaceWithUnderScore(clusterName)).toLowerCase();
    }

    private String generateUserNameForClusterAgentService(String clusterName) {
        return ("cg-" + this.developerAlias + "-public-" + Utils.removeAllSpaceWithUnderScore(clusterName)).toLowerCase();
    }
}
