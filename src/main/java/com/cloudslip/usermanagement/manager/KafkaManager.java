package com.cloudslip.usermanagement.manager;

import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;

@Service
public class KafkaManager implements KafkaManagerInterface {

    @Autowired
    private Environment env;


    @Override
    public boolean createTopic(String topicName) {
        boolean success = true;
        ZkClient zkClient = null;
        ZkUtils zkUtils = null;

        try {
            String zookeeperHosts = System.getenv("ZOOKEEPER_HOSTS");

            if(zookeeperHosts == null) {
                zookeeperHosts = env.getProperty("zookeeper.hosts");
                if(zookeeperHosts == null) {
                    zookeeperHosts = "localhost:2181"; // If multiple zookeeper then -> String zookeeperHosts = "192.168.20.1:2181,192.168.20.2:2181";
                }
            }
            int sessionTimeOutInMs = 15 * 1000; // 15 secs
            int connectionTimeOutInMs = 10 * 1000; // 10 secs

            zkClient = new ZkClient(zookeeperHosts, sessionTimeOutInMs, connectionTimeOutInMs, ZKStringSerializer$.MODULE$);
            zkUtils = new ZkUtils(zkClient, new ZkConnection(zookeeperHosts), false);

            int noOfPartitions = 1;
            int noOfReplication = 1;

            Properties topicConfiguration = new Properties();
            AdminUtils.createTopic(zkUtils, topicName, noOfPartitions, noOfReplication, topicConfiguration, RackAwareMode.Disabled$.MODULE$);

        } catch (Exception ex) {
            success = false;
            ex.printStackTrace();

        } finally {
            if (zkClient != null) {
                zkClient.close();
            }
        }
        return success;
    }


    @Override
    public boolean produceMessage(String topicName, String key, String message) {
        boolean success = true;

        String kafkaBroker = System.getenv("KAFKA_BROKER");

        if(kafkaBroker == null) {
            kafkaBroker = env.getProperty("kafka.broker");
            if(kafkaBroker == null) {
                kafkaBroker = "localhost:9092";
            }
        }

        Properties properties = new Properties();
        properties.put("bootstrap.servers", kafkaBroker);
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer kafkaProducer = new KafkaProducer(properties);

        try {
            kafkaProducer.send(new ProducerRecord(topicName, key, message));

        } catch (Exception e){
            success = false;
            e.printStackTrace();

        } finally {

            kafkaProducer.close();

        }
        return success;
    }

    @Override
    public List<String> consumeMessage(String topic) {
        return null;
    }

    @Override
    public boolean commitOffset(String topic, int offset) {
        return false;
    }


    @Override
    public boolean deleteTopic(String topicName) {
        return false;
    }
}
