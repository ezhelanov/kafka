package com.egor.kafka.properties;

import com.egor.kafka.serialization.GameReflectionDeserializer;

public class GameReflectionConsumerProperties extends ConsumerProperties {

    public GameReflectionConsumerProperties() {
        super();
    }

    public GameReflectionConsumerProperties(String groupId, boolean enableAutoCommit, String autoOffsetReset) {
        super(groupId, enableAutoCommit, autoOffsetReset);
    }

    public GameReflectionConsumerProperties(String groupId, boolean enableAutoCommit, String autoOffsetReset, int autoCommitIntervalMs) {
        super(groupId, enableAutoCommit, autoOffsetReset, autoCommitIntervalMs);
    }


    @Override
    protected void putValueDeserializer() {
        put("value.deserializer", GameReflectionDeserializer.class.getName());
    }
}
