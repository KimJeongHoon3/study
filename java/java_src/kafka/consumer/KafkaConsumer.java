package com.biz.bgmsgw.kafka.consumer;

import com.biz.bgmsgw.service.report.passive.ReportPassiveImpl;
import com.biz.bgmsgw.vo.GwConstant;
import io.netty.channel.ChannelHandlerContext;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class KafkaConsumer {
    private String bootstrapServers;
    private String groupId;
    private int concurrencyCnt;
    private Logger logger= LoggerFactory.getLogger(KafkaConsumerTest.class);
    private ConcurrentMessageListenerContainer container;
    private ContainerProperties containerProperties;
//    private final Set<String> topicSetForCheck= Collections.synchronizedSet(new HashSet<>());
    private final Map<String,Map<String,KafkaRecordInfo>> topicPartitionRecordInfoMap=new ConcurrentHashMap<>(); //topic - key / (partition -key / kafkaRecord - value) -value

    private Map<String, List<ChannelHandlerContext>> ctxListMap;
    private ReportPassiveImpl reportPassiveImpl;

    public void start(String[] topics){
        MDC.put(GwConstant.MDC_KEY,GwConstant.MDC_KAFKA_CONSUMER_VALUE);
        try{
            Map<String, Object> consumerConfig = new HashMap<>();
            // list of host:port pairs used for establishing the initial connections to the Kakfa cluster
            consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            consumerConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            consumerConfig.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,false);
            consumerConfig.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG,"1");
            DefaultKafkaConsumerFactory<String, String> kafkaConsumerFactory = new DefaultKafkaConsumerFactory<>(consumerConfig);

            KafkaReceiver kafkaReceiver=new KafkaReceiver();
            kafkaReceiver.setTopicPartitionRecordInfoMap(topicPartitionRecordInfoMap);
            kafkaReceiver.setReportPassiveImpl(reportPassiveImpl);
//        kafkaReceiver.setCtxListMap(ctxListMap);
            containerProperties = new ContainerProperties(topics);
            containerProperties.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
            containerProperties.setMessageListener(kafkaReceiver);

            container = new ConcurrentMessageListenerContainer<>(kafkaConsumerFactory, containerProperties);

            container.setConcurrency(concurrencyCnt);
            logger.info("kafka start..!");
            container.start();
        }catch(Exception e){
            throw e;
        }finally {
            MDC.clear();
        }

    }

    public void stop(){
        MDC.put(GwConstant.MDC_KEY,GwConstant.MDC_KAFKA_CONSUMER_VALUE);
        if(container!=null){
            logger.info("kafka stop..!");
            container.stop();
        }
        MDC.clear();
    }

    public List<String> getTopicList(){
        return Arrays.asList(containerProperties.getTopics());
    }

    public Map<String, Map<String, KafkaRecordInfo>> getTopicPartitionRecordInfoMap() {
        return topicPartitionRecordInfoMap;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setConcurrencyCnt(int concurrencyCnt) {
        this.concurrencyCnt = concurrencyCnt;
    }

    public void setReportPassiveImpl(ReportPassiveImpl reportPassiveImpl) {
        this.reportPassiveImpl=reportPassiveImpl;
    }

    //    public void setCtxListMap(Map<String, List<ChannelHandlerContext>> ctxListMap) {
//        this.ctxListMap=ctxListMap;
//    }


}
