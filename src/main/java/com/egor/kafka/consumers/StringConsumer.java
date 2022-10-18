package com.egor.kafka.consumers;

import com.egor.kafka.services.StringConsumerService;
import lombok.Getter;
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


    @Getter
    private final Thread thread = new Thread(() -> {

        while (true) {
            var messages = new ArrayList<String>();

            log.debug("Start poll");
            for (var record : poll(Duration.ofMillis(1000))) {
                log.trace("partition={}, offset={}, key={}, value={}, timestmap={}", record.partition(), record.offset(), record.key(), record.value(), record.timestamp());
                messages.add(record.value());
            }
            log.debug("End poll");

            stringConsumerService.getTotalReadMessages().addAll(messages);
        }

    });;


    public StringConsumer(Properties properties) {
        super(properties);
    }


}
