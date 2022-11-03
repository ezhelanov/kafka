package com.egor.kafka.utils;

import com.egor.kafka.properties.TablesProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.state.Stores;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TablesUtils {

    public KafkaStreams table(String topic, String groupId, String storeName) {

        Serde<String> stringSerde = Serdes.String();
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        KTable<String, String> table = streamsBuilder.table(topic,
                Consumed.with(Topology.AutoOffsetReset.EARLIEST),
                Materialized.<String, String>as(Stores.inMemoryKeyValueStore(storeName))
                        .withKeySerde(stringSerde)
                        .withValueSerde(stringSerde)
        );
        table.toStream().print(Printed.<String, String>toSysOut().withLabel("Table"));

        Topology topology = streamsBuilder.build();
        log.info(topology.describe().toString());

        return new KafkaStreams(topology, new TablesProperties(groupId));
    }

}
