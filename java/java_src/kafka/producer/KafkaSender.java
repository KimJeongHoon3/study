package com.biz.bgmsgw.kafka.producer;

import com.biz.bgmsgw.vo.GwConstant;
import com.biz.bgmsgw.vo.common.FaultQueueVo;
import com.biz.bgmsgw.vo.msg.queue.CommonMsgQueueVo;
import com.google.gson.Gson;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.TimeUnit;

public class KafkaSender {
    private static final Logger logger = LoggerFactory.getLogger(KafkaSender.class);
    private Gson gson=new Gson();
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void send(String topic, FaultQueueVo faultQueueVo) throws Exception{
        String[] spArr=faultQueueVo.getBsid().split("\\|");
        send(topic,gson.toJson(faultQueueVo),"",spArr[0],spArr[1]);
    }

    public void send(String topic, CommonMsgQueueVo commonMsgQueueVo) throws Exception{
        send(topic,gson.toJson(commonMsgQueueVo),commonMsgQueueVo.getClientMsgKey(),commonMsgQueueVo.getBsid(),commonMsgQueueVo.getAtaId());
    }

    public void send(String topic,String payload,String clientMsgKey,String clientId,String ataId) throws Exception{
        MDC.put(GwConstant.MDC_KEY,GwConstant.MDC_KAFKA_PRODUCER_VALUE);
        logger.info("sending clientMsgKey='{}', clientId='{}', ataId='{}', topic='{}', payload='{}'",clientMsgKey,clientId,ataId,topic, payload);
//        logger.info("sending clientMsgKey='{}', clientId='{}', ataId='{}', topic='{}', payload='{}'",clientMsgKey,clientId,ataId,topic, Base64.encodeBase64String(payload.getBytes(GwConstant.CHARSET)); /////////배포시 주석해제
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, payload);

        try{
            future.get(1, TimeUnit.SECONDS);
        }catch(Exception e){
            throw new Exception(String.format("kafka send error -  clientMsgKey='%s', clientId='%s', ataId='%s', topic='%s', payload='%s'",clientMsgKey,clientId,ataId,topic, Base64.encodeBase64String(payload.getBytes(GwConstant.CHARSET))));
        }finally {
            MDC.put(GwConstant.MDC_KEY,clientId+"_"+ataId);
        }
    }
}

