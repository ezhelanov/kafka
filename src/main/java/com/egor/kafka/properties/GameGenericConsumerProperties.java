package com.egor.kafka.properties;

import com.egor.kafka.serialization.GameGenericDeserializer;

public class GameGenericConsumerProperties extends ConsumerProperties {

    public GameGenericConsumerProperties() {
        super();
    }

    public GameGenericConsumerProperties(String groupId, boolean enableAutoCommit, String autoOffsetReset) {
        super(groupId, enableAutoCommit, autoOffsetReset);
    }

    public GameGenericConsumerProperties(String groupId, boolean enableAutoCommit, String autoOffsetReset, int autoCommitIntervalMs) {
        super(groupId, enableAutoCommit, autoOffsetReset, autoCommitIntervalMs);
    }


    @Override
    protected void putValueDeserializer() {
        put("value.deserializer", GameGenericDeserializer.class.getName());
    }
}
