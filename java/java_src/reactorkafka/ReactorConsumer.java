package com.biz.netty.test.reactorkafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.*;
import org.springframework.kafka.support.Acknowledgment;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOffset;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ReactorConsumer {
    Logger logger = LoggerFactory.getLogger(ReactorConsumer.class);
    public void start(){
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.16.0.55:9092");
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "sample-group");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,false);
        consumerProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG,"100");

        ReceiverOptions<String, String> receiverOptions =
                ReceiverOptions.<String, String>create(consumerProps)
                        .subscription(Collections.singleton("jeremy_test_test"))
                        .addAssignListener(partitions -> logger.info("onPartitionsAssigned {}", partitions))
                        .addRevokeListener(partitions -> logger.info("onPartitionsRevoked {}", partitions));

        Flux<ReceiverRecord<String,String>> flux=KafkaReceiver.create(receiverOptions).receive().log()
                .flatMap(t -> Flux.just(t).subscribeOn(Schedulers.boundedElastic()).doOnNext(receiverRecord -> logger.info(receiverRecord.toString())));

//                .doOnNext(this::nextOnTestFunc1);
//                .publishOn(Schedulers.boundedElastic())
//                .doOnNext(this::nextOnTestFunc2);
        flux.subscribe(/*record -> {
            ReceiverOffset receiverOffset=record.receiverOffset();
            logger.info(String.format("value : %s, topic :%s, partitions : %s, offset : %s",record.value(),record.topic(),record.partition(),receiverOffset.offset()));
        }*/);


    }

    /*private <V> V testFunc2(ReceiverRecord<String, String> record) {
    }*/


    Function<ReceiverRecord<String,String>,ReceiverRecord<String,String>> function1=(record)->{
        ReceiverOffset receiverOffset=record.receiverOffset();
        logger.info(String.format("value : %s, topic :%s, partitions : %s, offset : %s",record.value(),record.topic(),record.partition(),receiverOffset.offset()));
        return record;
    };

    public void nextOnTestFunc1(ReceiverRecord<String,String> record){
        ReceiverOffset receiverOffset=record.receiverOffset();
        logger.info(String.format("value : %s, topic :%s, partitions : %s, offset : %s",record.value(),record.topic(),record.partition(),receiverOffset.offset()));
    }

    public void nextOnTestFunc2(ReceiverRecord<String,String> record){
        ReceiverOffset receiverOffset=record.receiverOffset();
        logger.info(String.format("value : %s, topic :%s, partitions : %s, offset : %s",record.value(),record.topic(),record.partition(),receiverOffset.offset()));
    }

    public Flux<ReceiverRecord<String,String>> testFunc1(ReceiverRecord<String,String> record){
        ReceiverOffset receiverOffset=record.receiverOffset();
        logger.info(String.format("value : %s, topic :%s, partitions : %s, offset : %s",record.value(),record.topic(),record.partition(),receiverOffset.offset()));
        return Flux.just(record);
    }

    public Flux<ReceiverRecord<String,String>> testFunc2(ReceiverRecord<String,String> record){
        ReceiverOffset receiverOffset=record.receiverOffset();
        logger.info(String.format("value : %s, topic :%s, partitions : %s, offset : %s",record.value(),record.topic(),record.partition(),receiverOffset.offset()));
        return Flux.just(record);
    }

    public void startBatchListener(){
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.16.0.55:9092");
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "sample-group");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,false);
        consumerProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG,"50");

        DefaultKafkaConsumerFactory<String, String> kafkaConsumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps);
//        kafkaReceiver.setCtxListMap(ctxListMap);
        ContainerProperties containerProperties = new ContainerProperties("jeremy_test_test");
        containerProperties.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        containerProperties.setMessageListener(new BatchAcknowledgingConsumerAwareMessageListener<Integer,String>() {

            @Override
            public void onMessage(List<ConsumerRecord<Integer, String>> data, Acknowledgment acknowledgment, Consumer<?, ?> consumer) {
                System.out.println("data.size():"+data.size());
                for(ConsumerRecord record:data){
                    batchFunc1(record);
//                    batchFunc2(record);
                }
                acknowledgment.acknowledge();
            }

        });
//        containerProperties.setConsumerRebalanceListener(new CustomRebalanceListener(ataIdRecordInfoMap));

        KafkaMessageListenerContainer container = new KafkaMessageListenerContainer<>(kafkaConsumerFactory, containerProperties);

//            container.getContainerProperties().setSyncCommits(false);
//            container.setBatchErrorHandler(new SeekToCurrentBatchErrorHandler());
//            container.setConcurrency(concurrencyCnt);

        logger.info("kafka start..!");
        container.start();

    }

    public void startBatchListenerListToFlux(){
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.16.0.55:9092");
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "sample-group");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,false);
        consumerProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG,"50");

        DefaultKafkaConsumerFactory<String, String> kafkaConsumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps);
//        kafkaReceiver.setCtxListMap(ctxListMap);
        ContainerProperties containerProperties = new ContainerProperties("jeremy_test_test");
        containerProperties.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        containerProperties.setMessageListener(new BatchAcknowledgingConsumerAwareMessageListener<Integer,String>() {

            @Override
            public void onMessage(List<ConsumerRecord<Integer, String>> data, Acknowledgment acknowledgment, Consumer<?, ?> consumer) {
                System.out.println("data.size():"+data.size());
                Disposable disposable=Flux.fromIterable(data)
                        .subscribeOn(
                                Schedulers.boundedElastic()
                        )
                        .doOnNext(record -> {
                            batchFunc1(record);
                        })
                        .subscribe();
//                disposable.dispose();
//                acknowledgment.acknowledge();
            }

        });
//        containerProperties.setConsumerRebalanceListener(new CustomRebalanceListener(ataIdRecordInfoMap));

        KafkaMessageListenerContainer container = new KafkaMessageListenerContainer<>(kafkaConsumerFactory, containerProperties);

//            container.getContainerProperties().setSyncCommits(false);
//            container.setBatchErrorHandler(new SeekToCurrentBatchErrorHandler());
//            container.setConcurrency(concurrencyCnt);

        logger.info("kafka start..!");
        container.start();

    }

    public void batchFunc1(ConsumerRecord record){
        logger.info(String.format("value : %s, topic :%s, partitions : %s, offset : %s",record.value(),record.topic(),record.partition(),record.offset()));

    }

    public void batchFunc2(ConsumerRecord record){
        logger.info(String.format("value : %s, topic :%s, partitions : %s, offset : %s",record.value(),record.topic(),record.partition(),record.offset()));
    }
}
