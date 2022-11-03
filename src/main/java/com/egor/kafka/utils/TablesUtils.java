package com.egor.kafka.utils;

import com.egor.kafka.properties.TablesProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.Stores;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TablesUtils {

    public KafkaStreams table(String topic,
                              String groupId,
                              String storeName,
                              long tableCommitIntervalMs) {

        Serde<String> stringSerde = Serdes.String();
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        // SOURCE-процессор
        KStream<String, String> sourceProcessor = streamsBuilder.stream(topic, Consumed.with(Topology.AutoOffsetReset.EARLIEST));
        // процессор
        KTable<String, String> tableProcessor = sourceProcessor.toTable(
                Materialized.<String, String>as(Stores.inMemoryKeyValueStore(storeName))
                        .withKeySerde(stringSerde)
                        .withValueSerde(stringSerde)
        );
        // процессор
        KStream<String, String> processor = tableProcessor.toStream();
        // процессор
        processor.print(Printed.<String, String>toSysOut().withLabel("Table"));

        Topology topology = streamsBuilder.build();
        log.info(topology.describe().toString());

        return new KafkaStreams(topology, new TablesProperties(groupId, tableCommitIntervalMs));
    }

    public KafkaStreams table2(String topic,
                               String groupId,
                               String storeName,
                               long tableCommitIntervalMs) {

        Serde<String> stringSerde = Serdes.String();
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        // процессор (со встроенным SOURCE-процессором)
        KTable<String, String> tableProcessor = streamsBuilder.table(topic,
                Consumed.with(Topology.AutoOffsetReset.EARLIEST), // для SOURCE-процессора
                Materialized.<String, String>as(Stores.inMemoryKeyValueStore(storeName))
                        .withKeySerde(stringSerde)
                        .withValueSerde(stringSerde)
        );
        // процессор
        KStream<String, String> processor = tableProcessor.toStream();
        // процессор
        processor.print(Printed.<String, String>toSysOut().withLabel("Table"));

        Topology topology = streamsBuilder.build();
        log.info(topology.describe().toString());

        return new KafkaStreams(topology, new TablesProperties(groupId, tableCommitIntervalMs));
    }

}
