package com.egor.kafka.properties;

import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

public class ProducerProperties extends Properties {

    public ProducerProperties() {
        put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        putValueSerializer();
        put(ACKS_CONFIG, "1");
        put(MAX_BLOCK_MS_CONFIG, "5000");
    }

    public ProducerProperties(long lingerMs, String acks, long batchSizeBytes) {
        this();
        put(LINGER_MS_CONFIG, String.valueOf(lingerMs));
        put(ACKS_CONFIG, acks);
        put(BATCH_SIZE_CONFIG, String.valueOf(batchSizeBytes));
    }

    protected void putValueSerializer() {
        put(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    }
}
