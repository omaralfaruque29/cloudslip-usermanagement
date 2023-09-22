package com.cloudslip.usermanagement.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("cloudslip.kafka.producer")
public class KafkaCloudslipProducerProperties extends KafkaCommonProducerProperties {

}
