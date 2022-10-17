package com.egor.kafka.properties;

import java.util.Properties;

public class ProducerProperties extends Properties {

    public ProducerProperties() {
        put("bootstrap.servers", "localhost:9092");
        put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        put("acks", "1");
        put("max.block.ms", "5000");
    }
}
