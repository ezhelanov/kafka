package com.egor.kafka.utils;

import com.egor.kafka.properties.StreamsProperties;
import com.egor.kafka.transformers.CountValueTransformer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StreamsUtils {

    public KafkaStreams simple(String topic, String groupId) {

        Serde<String> stringSerde = Serdes.String();
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        // SOURCE-процессор
        KStream<String, String> sourceProcessor = streamsBuilder.stream(topic, Consumed.with(stringSerde, stringSerde).withOffsetResetPolicy(Topology.AutoOffsetReset.EARLIEST));
        // процессор (обычный)
        KStream<String, String> processor = sourceProcessor.mapValues(value -> value.toUpperCase());
        // процессор (обычный)
        processor.print(Printed.<String, String>toSysOut().withLabel("UPPERCASED:"));

        // строим топологию
        Topology topology = streamsBuilder.build();
        log.info(topology.describe().toString());

        return new KafkaStreams(topology, new StreamsProperties(groupId));
    }

    public KafkaStreams predicate(String topic, String outTopic, String groupId) {

        Predicate<String, String> containsNumber = new Predicate<String, String>() {
            @Override
            public boolean test(String key, String value) {
                return value.matches(".*\\d.*");
            }
        };

        Serde<String> stringSerde = Serdes.String();
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        // SOURCE-процессор
        KStream<String, String> sourceProcessor = streamsBuilder.stream(topic, Consumed.with(stringSerde, stringSerde).withOffsetResetPolicy(Topology.AutoOffsetReset.EARLIEST));
        // процессор (обычный)
        KStream<String, String> processor = sourceProcessor.filter(containsNumber);
        // SINK-процессор
        processor.to(outTopic, Produced.with(stringSerde, stringSerde));
        // процессор (обычный)
        processor.print(Printed.<String, String>toSysOut().withLabel("CONTAINS_NUMBER:"));

        // строим топологию
        Topology topology = streamsBuilder.build();
        log.info(topology.describe().toString());

        return new KafkaStreams(topology, new StreamsProperties(groupId));
    }

    public KafkaStreams store(String topic, String groupId, String storeName) {

        Serde<String> stringSerde = Serdes.String();

        StoreBuilder<KeyValueStore<String, Integer>> storeBuilder = Stores.keyValueStoreBuilder(
                Stores.inMemoryKeyValueStore(storeName), stringSerde, Serdes.Integer()
        );

        StreamsBuilder streamsBuilder = new StreamsBuilder();
        streamsBuilder.addStateStore(storeBuilder);

        // SOURCE-процессор
        KStream<String, String> sourceProcessor = streamsBuilder.stream(topic, Consumed.with(stringSerde, stringSerde).withOffsetResetPolicy(Topology.AutoOffsetReset.EARLIEST));
        // процессор
        KStream<String, String> processor = sourceProcessor.transformValues(() -> new CountValueTransformer(storeName), storeName);
        // процессор
        processor.print(Printed.<String, String>toSysOut().withLabel("COUNTED"));

        // строим топологию
        Topology topology = streamsBuilder.build();
        log.info(topology.describe().toString());

        return new KafkaStreams(topology, new StreamsProperties(groupId));
    }


}
