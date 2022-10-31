package com.egor.kafka.properties;

import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class ProducerProperties extends Properties {

    public ProducerProperties() {
        put("bootstrap.servers", "localhost:9092");
        put("key.serializer", StringSerializer.class.getName());
        putValueSerializer();
        put("acks", "1");
        put("max.block.ms", "5000");
    }

    protected void putValueSerializer() {
        put("value.serializer", StringSerializer.class.getName());
    }
}
