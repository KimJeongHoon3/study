package com.biz.bgmsgw.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class KafkaConsumerTest {
    Logger logger= LoggerFactory.getLogger(KafkaConsumerTest.class);
    private String bootstrapServers="172.16.0.55:9092,172.16.0.55:9094,172.16.0.55:9095";
    ConcurrentMessageListenerContainer container;
    public void start(String topic){
        int k=Integer.parseInt(topic);
         String[] strArr=new String[k-1];
        for(int i=1;i<k;i++){
                strArr[i-1]=i+"";
            }
//        String[] strArr={"test-1","test-2"};

//        for(int i=0;i<1000;i++){
//            topic=i+"";
            Map<String, Object> consumerConfig = new HashMap<>();
            // list of host:port pairs used for establishing the initial connections to the Kakfa cluster
            consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, "groupId");
            consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            consumerConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            consumerConfig.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,false);
            consumerConfig.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG,"1");

            DefaultKafkaConsumerFactory<String, String> kafkaConsumerFactory = new DefaultKafkaConsumerFactory<>(consumerConfig);

            ContainerProperties containerProperties = new ContainerProperties(strArr);


            containerProperties.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
            KafkaReceiver kafkaReceiver=new KafkaReceiver();
        Set<String> topicSetForCheck= Collections.synchronizedSet(new HashSet<>());
//        kafkaReceiver.setTopicSetForCheck(topicSetForCheck);
            containerProperties.setMessageListener(kafkaReceiver);

            container = new ConcurrentMessageListenerContainer<>(kafkaConsumerFactory, containerProperties);

            container.setConcurrency(1);
            container.start();
//        }





    }

    public void stop(){
        logger.info("kafka stop..!");
        container.stop();

    }

    public void restart(){
        logger.info("kafka restart..!");
        start("120");

    }
}
