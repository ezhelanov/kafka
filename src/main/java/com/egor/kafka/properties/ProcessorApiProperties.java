package com.egor.kafka.properties;

import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;

public class ProcessorApiProperties extends TablesProperties {

    public ProcessorApiProperties(String groupId) {
        super(groupId);
        put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class);
    }

}
