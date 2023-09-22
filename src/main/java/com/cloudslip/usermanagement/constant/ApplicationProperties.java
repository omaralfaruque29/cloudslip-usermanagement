package com.cloudslip.usermanagement.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationProperties {

    @Value("${developer.alias}")
    private String DEVELOPER_ALIAS;

    @Value("${env.facade-service.base-url}")
    private String FACADE_SERVICE_BASE_URL;

    @Value("${env.pipeline-service.base-url}")
    private String PIPELINE_SERVICE_BASE_URL;

    @Value("${env.listener-service.base-url}")
    private String LISTENER_SERVICE_BASE_URL;

    @Value("${env.listener-service.api-access-token}")
    private String LISTENER_SERVICE_API_ACCESS_TOKEN;

    @Value("${cloudslip.kafka.force-use}")
    private String KAFKA_FORCE_USE;

    public static final String DOCKER_HUB_REGISTRY_SERVER = "https://index.docker.io/v1/";


    public ApplicationProperties() {
    }

    public String getDeveloperAlias() {
        return DEVELOPER_ALIAS;
    }

    public String getFacadeServiceBaseUrl() {
        return FACADE_SERVICE_BASE_URL;
    }

    public String getPipelineServiceBaseUrl() {
        return PIPELINE_SERVICE_BASE_URL;
    }

    public String getListenerServiceBaseUrl() {
        return LISTENER_SERVICE_BASE_URL;
    }

    public String getListenerServiceApiAccessToken() {
        return LISTENER_SERVICE_API_ACCESS_TOKEN;
    }

    public boolean isKafkaForceUse() {
        try {
            return Boolean.valueOf(KAFKA_FORCE_USE);
        } catch (Exception ex) {
            return false;
        }
    }
}
