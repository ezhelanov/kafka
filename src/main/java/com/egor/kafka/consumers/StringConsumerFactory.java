package com.egor.kafka.consumers;

public interface StringConsumerFactory {

    DuplicatesStringConsumer get();

    StringConsumer get(String groupId, boolean enableAutoCommit, String autoOffsetReset, int autoCommitIntervalMs);
}
