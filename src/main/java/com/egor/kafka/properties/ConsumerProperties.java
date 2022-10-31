package com.egor.kafka.properties;

import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Properties;

public class ConsumerProperties extends Properties {

    public ConsumerProperties() {
        put("bootstrap.servers", "localhost:9092");
        put("key.deserializer", StringDeserializer.class.getName());
        putValueDeserializer();
    }

    public ConsumerProperties(String groupId, boolean enableAutoCommit, String autoOffsetReset) {
        this();
        put("group.id", groupId);
        put("enable.auto.commit", enableAutoCommit);
        put("auto.offset.reset", autoOffsetReset);
    }

    public ConsumerProperties(String groupId, boolean enableAutoCommit, String autoOffsetReset, int autoCommitIntervalMs) {
        this(groupId, enableAutoCommit, autoOffsetReset);
        put("auto.commit.interval.ms", autoCommitIntervalMs);
    }


    protected void putValueDeserializer() {
        put("value.deserializer", StringDeserializer.class.getName());
    }
}
