package com.egor.kafka.controllers;

import com.egor.kafka.properties.StreamsProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("kafka/streams")
public class StreamsController {

    @PostMapping("simple")
    public void simple(@RequestParam String topic,
                       @RequestParam String groupId) throws InterruptedException {

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

        KafkaStreams kafkaStreams = new KafkaStreams(topology, new StreamsProperties(groupId));
        log.info("Streaming app started!");
        kafkaStreams.start();
        Thread.sleep(10 * 1000);
        kafkaStreams.close();
        log.info("Streaming app closed!");
    }

    @PostMapping("predicate")
    public void predicate(@RequestParam String topic,
                          @RequestParam String outTopic,
                          @RequestParam String groupId) throws InterruptedException {

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

        KafkaStreams kafkaStreams = new KafkaStreams(topology, new StreamsProperties(groupId));
        log.info("Streaming app started!");
        kafkaStreams.start();
        Thread.sleep(10 * 1000);
        kafkaStreams.close();
        log.info("Streaming app closed!");
    }

}
