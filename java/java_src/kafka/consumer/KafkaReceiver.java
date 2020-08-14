package com.biz.bgmsgw.kafka.consumer;

import com.biz.bgmsgw.service.report.passive.ReportPassiveImpl;
import com.biz.bgmsgw.vo.GwConstant;
import com.biz.bgmsgw.vo.common.SendVo;
import com.biz.bgmsgw.vo.report.ReportReqVo;
import com.biz.bgmsgw.vo.report.queue.ReportQueueVo;
import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.kafka.support.Acknowledgment;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class KafkaReceiver implements AcknowledgingMessageListener<Integer, String>, ConsumerSeekAware {

    private ConsumerSeekCallback consumerSeekCallback;
    private Logger logger= LoggerFactory.getLogger(KafkaReceiver.class);
    private Set<KafkaRecordInfo> recordInfoSetForCheck;
    private Map<String,Map<String,KafkaRecordInfo>> topicPartitionRecordInfoMap; //topic - key / (partition -key / kafkaRecord - value) -value
    private ReportPassiveImpl reportPassiveImpl;
    private Gson gson=new Gson();
//    private Map<String, List<ChannelHandlerContext>> ctxListMap;

    @Override
    public void onMessage(ConsumerRecord<Integer, String> record, Acknowledgment acknowledgment) {
        MDC.put(GwConstant.MDC_KEY,GwConstant.MDC_KAFKA_CONSUMER_VALUE);
        String topic=record.topic();
        int partition=record.partition();
        String value=record.value();
        long offset=record.offset();

        try{
            logger.debug("record.topic() : "+topic+", record.partition() : "+partition+", record.values() : "+value+", record.offset() : "+offset);

            Map<String,KafkaRecordInfo> partitionRecordInfoMap= getParitionRecordInfoMap(topic);

            KafkaRecordInfo oldKafkaRecordInfo=partitionRecordInfoMap.get(partition+"");
            if(oldKafkaRecordInfo==null) {//처음 report 요청하는 데이터
                try{
                    logger.info("TRANSFER REPORT DATA TO reportPassiveImpl - record.topic() : "+topic+", record.partition() : "+partition+", record.values() : "+value+", record.offset() : "+offset);
                    handleFirstReportReq(record,partitionRecordInfoMap);
                }catch(Exception e){
                    partitionRecordInfoMap.remove(partition+"");
                    logger.error(e.getMessage(),e);
                }

            }else {
                if(oldKafkaRecordInfo.isSendComplete()) {
//                logger.info("try to remove partition");
                    partitionRecordInfoMap.remove(partition+"");
//                logger.info("success to remove partition");
                    acknowledgment.acknowledge(); //offset 저장
                    return;
                }

            }

            consumerSeekCallback.seek(topic, partition, offset);
        }catch(Exception e){
            logger.error(e.getMessage(),e);
            consumerSeekCallback.seek(topic, partition, offset);
        }finally {
            MDC.clear();
        }

    }

    private void handleFirstReportReq(ConsumerRecord<Integer, String> record, Map<String, KafkaRecordInfo> partitionRecordInfoMap) throws Exception {
        KafkaRecordInfo newKafkaRecordInfo=getKafkaRecordInfo(record);
        partitionRecordInfoMap.put(record.partition()+"",newKafkaRecordInfo);

        ReportQueueVo reportQueueVo=gson.fromJson(record.value(), ReportQueueVo.class);
        ReportReqVo reportReqVo=parseToReportReqVo(reportQueueVo,record.partition());
        transferToReportPassiveImpl(reportReqVo);

    }

    private void transferToReportPassiveImpl(ReportReqVo reportReqVo) throws Exception{
        reportPassiveImpl.sendReport(reportReqVo);
    }

    private KafkaRecordInfo getKafkaRecordInfo(ConsumerRecord<Integer, String> record) {
        return new KafkaRecordInfo(record.topic(),record.value(),record.offset(),false);
    }

    private ReportReqVo parseToReportReqVo(ReportQueueVo reportQueueVo,int partition) {
        return new ReportReqVo(GwConstant.REPORT_REQ,reportQueueVo.getResultCode(),GwConstant.BGMS_GW_NAME,reportQueueVo.getClientMsgKey(),
                reportQueueVo.getRecipientOrder(),reportQueueVo.getRecipientNum(),"10001",reportQueueVo.getReceivedAt(),
                reportQueueVo.getAtaTag(),reportQueueVo.getBsid(), reportQueueVo.getAtaId(),partition+"");
    }

    private synchronized Map<String, KafkaRecordInfo> getParitionRecordInfoMap(String topic) {
        Map<String, KafkaRecordInfo> partitionRecordInfoMap= topicPartitionRecordInfoMap.get(topic);
        if(partitionRecordInfoMap==null) {
            partitionRecordInfoMap= new ConcurrentHashMap<>();
            topicPartitionRecordInfoMap.put(topic,partitionRecordInfoMap);
        }
        return partitionRecordInfoMap;
    }


    @Override
    public void registerSeekCallback(ConsumerSeekCallback consumerSeekCallback) {
        this.consumerSeekCallback = consumerSeekCallback;
    }

    @Override
    public void onPartitionsAssigned(Map<TopicPartition, Long> map, ConsumerSeekCallback consumerSeekCallback) {

        // nothing is needed here for this program
    }

    @Override
    public void onIdleContainer(Map<TopicPartition, Long> map, ConsumerSeekCallback consumerSeekCallback) {

        // nothing is needed here for this program
    }

    public void setTopicPartitionRecordInfoMap(Map<String, Map<String, KafkaRecordInfo>> topicPartitionRecordInfoMap) {
        this.topicPartitionRecordInfoMap=topicPartitionRecordInfoMap;
    }

    public void setReportPassiveImpl(ReportPassiveImpl reportPassiveImpl) {
        this.reportPassiveImpl=reportPassiveImpl;
    }
}