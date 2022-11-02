package com.egor.kafka.properties;

import org.apache.kafka.streams.StreamsConfig;

import java.util.Properties;

public class StreamsProperties extends Properties {

    public StreamsProperties(String groupId) {
        put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        put(StreamsConfig.APPLICATION_ID_CONFIG, groupId);
    }
}
