package com.biz.bgmsgw.kafka.consumer;

public class KafkaRecordInfo {
    String topic;
    String value;
    long offset;
    boolean sendComplete;

    public KafkaRecordInfo(String topic, String value, long offset, boolean sendComplete) {
        this.topic = topic;
        this.value = value;
        this.offset = offset;
        this.sendComplete = sendComplete;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public boolean isSendComplete() {
        return sendComplete;
    }

    public void setSendComplete(boolean sendComplete) {
        this.sendComplete = sendComplete;
    }

    @Override
    public String toString() {
        return "KafkaRecordInfo{" +
                "topic='" + topic + '\'' +
                ", value='" + value + '\'' +
                ", offset='" + offset + '\'' +
                ", sendComplete=" + sendComplete +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof KafkaRecordInfo){
            KafkaRecordInfo compareData=(KafkaRecordInfo)obj;
            if(this.topic.equals(compareData.topic)){
                return true;
            }
        }
        return false;
    }
}
