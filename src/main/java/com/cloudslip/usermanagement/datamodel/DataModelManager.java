package com.cloudslip.usermanagement.datamodel;


import com.cloudslip.usermanagement.constant.ApplicationProperties;
import com.cloudslip.usermanagement.core.CustomRestTemplate;
import com.cloudslip.usermanagement.enums.ServerSettingGroup;
import com.cloudslip.usermanagement.enums.ServerSettingKey;
import com.cloudslip.usermanagement.enums.ServerSettingType;
import com.cloudslip.usermanagement.kafka.KafkaPublisher;
import com.cloudslip.usermanagement.model.ServerSetting;
import com.cloudslip.usermanagement.repository.KubeClusterRepository;
import com.cloudslip.usermanagement.repository.EnvironmentOptionRepository;
import com.cloudslip.usermanagement.repository.RegionRepository;
import com.cloudslip.usermanagement.repository.ServerSettingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class DataModelManager {

    private Logger log = LogManager.getLogger(DataModelManager.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ServerSettingRepository serverSettingRepository;

    @Autowired
    private EnvironmentOptionRepository environmentOptionRepository;

    @Autowired
    private KubeClusterRepository kubeClusterRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private KafkaPublisher kafkaPublisher;

    @Autowired
    private CustomRestTemplate restTemplate;

    @Autowired
    private ApplicationProperties applicationProperties;

    private List<AbstractDataModel> dataModelList = Arrays.asList(new DataModelV001());

    private ServerSetting dataModelSettings;


    public void runUpdate(Environment env) {
        Optional<ServerSetting> dataModelServerSettings = serverSettingRepository.findByKey(ServerSettingKey.DATA_MODEL_VERSION);
        if(!dataModelServerSettings.isPresent()) {
            dataModelSettings = new ServerSetting(ServerSettingGroup.DATA_MODEL, ServerSettingKey.DATA_MODEL_VERSION, "0", "0", ServerSettingType.HIDDEN);
            dataModelSettings.setCreateDate(String.valueOf(LocalDateTime.now()));
            dataModelSettings = serverSettingRepository.save(dataModelSettings);
        } else {
            dataModelSettings = dataModelServerSettings.get();
        }
        for (AbstractDataModel dataModel : dataModelList) {
            dataModel.execute(this, Integer.parseInt(dataModelSettings.getCurrentValue()), env);
        }
    }

    protected ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    protected ServerSettingRepository getServerSettingRepository() {
        return serverSettingRepository;
    }

    protected EnvironmentOptionRepository getEnvironmentOptionRepository() {
        return environmentOptionRepository;
    }

    protected KubeClusterRepository getKubeClusterRepository() {
        return kubeClusterRepository;
    }

    protected RegionRepository getRegionRepository() {
        return regionRepository;
    }

    protected KafkaPublisher getKafkaPublisher() {
        return kafkaPublisher;
    }

    protected CustomRestTemplate getRestTemplate() {
        return restTemplate;
    }

    protected ApplicationProperties getApplicationProperties() {
        return applicationProperties;
    }

    protected ServerSetting getDataModelSettings() {
        return dataModelSettings;
    }

    protected void setDataModelSettings(ServerSetting dataModelSettings) {
        this.dataModelSettings = dataModelSettings;
    }
}