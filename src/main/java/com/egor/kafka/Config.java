package com.egor.kafka;

import com.egor.kafka.consumers.StringConsumer;
import com.egor.kafka.consumers.StringConsumerFactory;
import com.egor.kafka.properties.ConsumerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class Config {

    @Bean
    @Scope("prototype")
    public StringConsumer stringConsumer(String groupId,
                                         boolean enableAutoCommit,
                                         String autoOffsetReset,
                                         int autoCommitIntervalMs) {
        return new StringConsumer(new ConsumerProperties(groupId, enableAutoCommit, autoOffsetReset, autoCommitIntervalMs));
    }

    @Bean
    public StringConsumerFactory stringConsumerFactory() {
        return new StringConsumerFactory() {
            @Override
            public StringConsumer get(String groupId,
                                      boolean enableAutoCommit,
                                      String autoOffsetReset,
                                      int autoCommitIntervalMs) {
                return stringConsumer(groupId, enableAutoCommit, autoOffsetReset, autoCommitIntervalMs);
            }
        };
    }
}
