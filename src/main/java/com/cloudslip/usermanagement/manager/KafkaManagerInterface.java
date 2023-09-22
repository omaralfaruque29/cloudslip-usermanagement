package com.cloudslip.usermanagement.manager;

import java.util.List;

public interface KafkaManagerInterface {

    public boolean createTopic(String topicName);

    public boolean produceMessage(String topicName, String key, String message);

    public List<String> consumeMessage(String topic);

    public boolean commitOffset(String topic, int offset);

    public boolean deleteTopic(String topicName);

}
