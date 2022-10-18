package com.egor.kafka.consumers;

public abstract class StringConsumerFactory {

    public abstract StringConsumer get(
            String groupId,
            boolean enableAutoCommit,
            String autoOffsetReset,
            int autoCommitIntervalMs
    );

}
