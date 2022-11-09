package com.egor.kafka.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StoresService {

    public ReadOnlyKeyValueStore<String, Object> getStore(KafkaStreams kafkaStreams, String storeName) {
        if (kafkaStreams.state() != KafkaStreams.State.RUNNING) {
            log.error("Cannot open store - {}", kafkaStreams.state().name());
            return null;
        }

        return kafkaStreams.store(
                StoreQueryParameters.fromNameAndType(storeName, QueryableStoreTypes.keyValueStore())
        );
    }

}
