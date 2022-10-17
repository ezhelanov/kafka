package com.egor.kafka.properties;

import java.util.Properties;

public class ConsumerProperties extends Properties {

    public ConsumerProperties() {
        put("bootstrap.servers", "localhost:9092");
        put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
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
}
