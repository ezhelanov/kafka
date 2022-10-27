package com.egor.kafka.consumers;

public interface StringConsumerFactory {

    StringConsumer get(String groupId, boolean enableAutoCommit, String autoOffsetReset, int autoCommitIntervalMs);
}
