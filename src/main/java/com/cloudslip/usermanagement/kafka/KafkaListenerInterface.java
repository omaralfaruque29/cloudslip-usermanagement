package com.cloudslip.usermanagement.kafka;

import com.cloudslip.usermanagement.kafka.dto.KafkaMessage;

import java.util.List;

public interface KafkaListenerInterface {

    public void subscribeTopic(String topicName, String consumerGroupId);

    public void unsubscribeTopic(String topicName);

    public List<KafkaMessage> consumeMessage(String topicName);

    public boolean commitOffset(String topic, int offset);

}
