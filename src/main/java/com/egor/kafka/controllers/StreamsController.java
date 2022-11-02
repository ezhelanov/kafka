package com.egor.kafka.controllers;

import com.egor.kafka.properties.StreamsProperties;
import com.egor.kafka.services.StreamsService;
import com.egor.kafka.transformers.CountValueTransformer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("kafka/streams")
public class StreamsController {

    @Autowired
    private StreamsService streamsService;


    @PostMapping("simple")
    public void simple(@RequestParam String topic, @RequestParam String groupId) {

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

        streamsService.startAndCloseStream(topology, new StreamsProperties(groupId));
    }

    @PostMapping("predicate")
    public void predicate(@RequestParam String topic, @RequestParam String outTopic, @RequestParam String groupId) {

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

        streamsService.startAndCloseStream(topology, new StreamsProperties(groupId));
    }

    @PostMapping("store")
    public void store(@RequestParam String topic, @RequestParam String groupId, @RequestParam String storeName) {

        Serde<String> stringSerde = Serdes.String();

        KeyValueBytesStoreSupplier storeSupplier = Stores.inMemoryKeyValueStore(storeName);
        StoreBuilder<KeyValueStore<String, Integer>> storeBuilder = Stores.keyValueStoreBuilder(storeSupplier, stringSerde, Serdes.Integer());

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

        streamsService.startAndCloseStream(topology, new StreamsProperties(groupId));
    }




}
