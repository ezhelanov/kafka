package com.egor.kafka.consumers;

import com.egor.kafka.properties.ConsumerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class StringConsumerConfig {

    @Bean
    @Scope("prototype")
    public StringConsumer stringConsumer(String groupId, boolean enableAutoCommit, String autoOffsetReset, int autoCommitIntervalMs) {
        if (groupId == null) {
            return new StringConsumer(new ConsumerProperties());
        }
        return new StringConsumer(new ConsumerProperties(groupId, enableAutoCommit, autoOffsetReset, autoCommitIntervalMs));
    }

    @Bean
    public StringConsumerFactory stringConsumerFactory() {
        return this::stringConsumer;
    }
}
