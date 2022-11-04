package com.egor.kafka.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StreamsService {

    public void printCountsStore(KafkaStreams kafkaStreams) {
        if (kafkaStreams.state() != KafkaStreams.State.RUNNING) {
            log.error("Cannot open store - {}", kafkaStreams.state().name());
            return;
        }

        ReadOnlyKeyValueStore<String, Integer> storeView = kafkaStreams.store(
                StoreQueryParameters.fromNameAndType("counts", QueryableStoreTypes.keyValueStore())
        );
        log.info("---counts---");
        storeView.all().forEachRemaining(
                keyValue -> log.info("key - '{}', value - {}", keyValue.key, keyValue.value)
        );
        log.info("------------");
    }

    public ReadOnlyKeyValueStore<String, String> getStringStore(KafkaStreams kafkaStreams, String storeName) {
        if (kafkaStreams.state() != KafkaStreams.State.RUNNING) {
            log.error("Cannot open store - {}", kafkaStreams.state().name());
            return null;
        }

/*        ReadOnlyKeyValueStore<String, Integer> storeView =*/return kafkaStreams.store(
                StoreQueryParameters.fromNameAndType(storeName, QueryableStoreTypes.keyValueStore())
        );
//        log.info("---counts---");
//        storeView.all().forEachRemaining(
//                keyValue -> log.info("key - '{}', value - {}", keyValue.key, keyValue.value)
//        );
//        log.info("------------");
    }

}
