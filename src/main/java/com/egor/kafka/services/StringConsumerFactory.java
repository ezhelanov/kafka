package com.egor.kafka.services;

import com.egor.kafka.consumers.StringConsumer;

public abstract class StringConsumerFactory {

    public abstract StringConsumer get(
            String groupId,
            boolean enableAutoCommit,
            String autoOffsetReset,
            int autoCommitIntervalMs
    );

}
