package com.cloudslip.usermanagement.core;


import com.cloudslip.usermanagement.constant.ApplicationProperties;
import com.cloudslip.usermanagement.enums.ServerSettingGroup;
import com.cloudslip.usermanagement.enums.ServerSettingKey;
import com.cloudslip.usermanagement.enums.ServerSettingType;
import com.cloudslip.usermanagement.kafka.KafkaListener;
import com.cloudslip.usermanagement.kafka.KafkaPublisher;
import com.cloudslip.usermanagement.model.ServerSetting;
import com.cloudslip.usermanagement.repository.ServerSettingRepository;
import com.cloudslip.usermanagement.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;


@Component
public class CreateAndSubscribeTopicForClusterAgentResponse {


    private Logger log = LogManager.getLogger(CreateAndSubscribeTopicForClusterAgentResponse.class);

    private final String clusterAgentResponseTopic = "cluster-agent-response";

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private ServerSettingRepository serverSettingRepository;

    @Autowired
    private KafkaPublisher kafkaPublisher;

    @Autowired
    private KafkaListener kafkaListener;

    private ServerSetting clusterAgentResponseTopicInitServerSetting;


    public void execute(Environment env) {
        if(applicationProperties.isKafkaForceUse() || (env.getActiveProfiles().length > 0 && Utils.isStringEquals(env.getActiveProfiles()[0], "prod"))) {
            Optional<ServerSetting> carTopicInitServerSetting = serverSettingRepository.findByKey(ServerSettingKey.KAFKA_TOPIC_CLUSTER_AGENT_RESPONSE_INIT);
            if (!carTopicInitServerSetting.isPresent() || !(carTopicInitServerSetting.get().getCurrentValue().equals("DONE") || carTopicInitServerSetting.get().getCurrentValue() == "DONE")) {

                try {
                    createTopic();
                    createConsumerGroupForTopic();
                } catch (Exception ex) {
                    log.error(ex.getMessage());
                    return;
                }

                clusterAgentResponseTopicInitServerSetting = new ServerSetting(ServerSettingGroup.KAFKA_TOPIC, ServerSettingKey.KAFKA_TOPIC_CLUSTER_AGENT_RESPONSE_INIT, "PENDING", "DONE", ServerSettingType.HIDDEN);
                clusterAgentResponseTopicInitServerSetting.setCreateDate(String.valueOf(LocalDateTime.now()));
                clusterAgentResponseTopicInitServerSetting = serverSettingRepository.save(clusterAgentResponseTopicInitServerSetting);
            }
        }
    }

    private void createTopic() {
        log.info("Creating Kafka Topic for Vpc Agent Response");
        kafkaPublisher.createTopic("kt-" + applicationProperties.getDeveloperAlias() + "-" + clusterAgentResponseTopic);
    }

    private void createConsumerGroupForTopic() {
        log.info("Creating Consumer Group for Vpc Agent Response Topic");
        kafkaListener.subscribeTopic("kt-" + applicationProperties.getDeveloperAlias() + "-" + clusterAgentResponseTopic, "cg-" + applicationProperties.getDeveloperAlias() + "-" + clusterAgentResponseTopic);
    }

}
