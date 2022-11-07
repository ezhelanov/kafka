package com.egor.kafka.properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;

public class TablesProperties extends StreamsProperties {

    public TablesProperties(String groupId, long tableCommitIntervalMs) {
        this(groupId);
        put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, String.valueOf(tableCommitIntervalMs)); // частота обновления таблицы
    }

    public TablesProperties(String groupId) {
        super(groupId);
        put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    }
}
