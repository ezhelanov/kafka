package com.egor.kafka.consumers;

import com.egor.kafka.services.StringConsumerService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Properties;

@Slf4j
public class StringConsumer extends KafkaConsumer<String, String> {

    @Autowired
    private StringConsumerService stringConsumerService;

    @Setter
    private boolean enabled;

    @Getter
    private final Thread thread = new Thread(() -> {

        while (enabled) {
            var messages = new ArrayList<String>();

            log.error("Start poll");
            for (var record : poll(Duration.ofMillis(1000))) {
                log.warn("partition={}, offset={}, key={}, value={}, timestmap={}", record.partition(), record.offset(), record.key(), record.value(), record.timestamp());
                messages.add(record.value());
            }
            log.error("End poll");

            stringConsumerService.getTotalReadMessages().addAll(messages);
        }

    });


    public StringConsumer(Properties properties) {
        super(properties);
    }


}
