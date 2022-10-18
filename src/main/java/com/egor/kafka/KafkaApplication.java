package com.egor.kafka;

import com.egor.kafka.consumers.StringConsumer;
import com.egor.kafka.properties.ConsumerProperties;
import com.egor.kafka.services.StringConsumerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

@SpringBootApplication
public class KafkaApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaApplication.class, args);
    }

    @Bean
    @Scope("prototype")
    public StringConsumer stringConsumer(String groupId, boolean enableAutoCommit, String autoOffsetReset, int autoCommitIntervalMs) {
        return new StringConsumer(new ConsumerProperties(groupId, enableAutoCommit, autoOffsetReset, autoCommitIntervalMs));
    }

    @Bean
    public StringConsumerFactory stringConsumerFactory() {
        return new StringConsumerFactory() {
            @Override
            public StringConsumer get(String groupId, boolean enableAutoCommit, String autoOffsetReset, int autoCommitIntervalMs) {
                return stringConsumer(groupId, enableAutoCommit, autoOffsetReset, autoCommitIntervalMs);
            }
        };
    }

}
