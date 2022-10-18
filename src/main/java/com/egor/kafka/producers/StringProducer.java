package com.egor.kafka.producers;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.*;

import java.util.Properties;

@Slf4j
public class StringProducer extends KafkaProducer<String, String> {

    public StringProducer(Properties properties) {
        super(properties);
    }
}
