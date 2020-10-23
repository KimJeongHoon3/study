package com.biz.netty.test.reactorkafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;

import java.util.HashMap;
import java.util.Map;

public class ReactorProducer {
    private final KafkaSender<String,String> sender;
    private Logger logger= LoggerFactory.getLogger(ReactorProducer.class);

    public ReactorProducer(){
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.16.0.55:9092");
        props.put(ProducerConfig.ACKS_CONFIG, "1");
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG,"30000"); //default 60000
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        SenderOptions<String,String> senderOption=SenderOptions.create(props);

        sender=KafkaSender.create(senderOption);
    }

    public void sendMessage(String topic,String payload){
        sender.send(Flux.just(SenderRecord.create(new ProducerRecord<String, String>(topic,payload),topic)))
                .doOnError(e -> logger.error(e.getMessage(),e))
                .subscribe(r -> {
                    RecordMetadata recordMetadata=r.recordMetadata();
                    logger.info("send success : "+recordMetadata.toString()+","+r.correlationMetadata());

                });
    }

}
