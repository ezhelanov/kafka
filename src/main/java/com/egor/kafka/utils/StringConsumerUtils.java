package com.egor.kafka.utils;

import com.egor.kafka.consumers.DuplicatesStringConsumer;
import com.egor.kafka.consumers.StringConsumer;
import com.egor.kafka.properties.ConsumerProperties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.stereotype.Component;

@Component
public class StringConsumerUtils {

    public StringConsumer get(String name,
                              String groupId,
                              boolean enableAutoCommit,
                              String autoOffsetReset,
                              int autoCommitIntervalMs,
                              int maxPollRecords,
                              int maxPartitionFetchBytes) {

        var properties = groupId == null ? new ConsumerProperties() : new ConsumerProperties(groupId, enableAutoCommit, autoOffsetReset, autoCommitIntervalMs);
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        properties.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, maxPartitionFetchBytes);

        var consumer = new StringConsumer(properties);
        consumer.setName(name);

        return consumer;
    }


    public DuplicatesStringConsumer getDuplicates(String name) {

        var consumer = new DuplicatesStringConsumer(new ConsumerProperties());
        consumer.setName(name);

        return consumer;
    }


}
