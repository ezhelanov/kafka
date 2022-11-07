package com.egor.kafka.utils;

import com.egor.kafka.properties.TablesProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class WindowsUtils {

    public KafkaStreams session(String topic,
                                String groupId,
                                long tableCommitIntervalMs,
                                long windowLengthSec,
                                long windowAfterEndSec) {

        Serde<String> stringSerde = Serdes.String();
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        SessionWindows sessionWindows = SessionWindows.ofInactivityGapAndGrace(Duration.ofSeconds(windowLengthSec), Duration.ofSeconds(windowAfterEndSec));

        KTable<Windowed<String>, Long> table = streamsBuilder.stream(topic, Consumed.with(stringSerde, stringSerde))
                .groupBy((key, value) -> value, Grouped.with(stringSerde, stringSerde))
                .windowedBy(sessionWindows)
                .count();

        table.toStream().foreach(
                (window, valueCount) -> System.out.println("tumble_window - " + window.window().start() + "/" + window.window().end() + ", value - " + window.key() + ", valueCount - " + valueCount)
        );

        Topology topology = streamsBuilder.build();
        System.out.println(topology.describe().toString());

        return new KafkaStreams(topology, new TablesProperties(groupId, tableCommitIntervalMs));
    }

    public KafkaStreams tumble(String topic,
                               String groupId,
                               long tableCommitIntervalMs,
                               long windowLengthSec,
                               long windowAfterEndSec) {

        Serde<String> stringSerde = Serdes.String();
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        TimeWindows tumbleWindow = TimeWindows.ofSizeAndGrace(Duration.ofSeconds(windowLengthSec), Duration.ofSeconds(windowAfterEndSec));

        KTable<Windowed<String>, Long> table = streamsBuilder.stream(topic, Consumed.with(stringSerde, stringSerde))
                .groupBy((key, value) -> value, Grouped.with(stringSerde, stringSerde))
                .windowedBy(tumbleWindow)
                .count();

        table.toStream().foreach(
                (window, valueCount) -> System.out.println("tumble_window - " + window.window().start() + "/" + window.window().end() + ", value - " + window.key() + ", valueCount - " + valueCount)
        );

        Topology topology = streamsBuilder.build();
        System.out.println(topology.describe().toString());

        return new KafkaStreams(topology, new TablesProperties(groupId, tableCommitIntervalMs));
    }

    public KafkaStreams hop(String topic,
                            String groupId,
                            long tableCommitIntervalMs,
                            long windowLengthSec,
                            long windowShiftSec,
                            long windowAfterEndSec) {

        Serde<String> stringSerde = Serdes.String();
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        TimeWindows hopWindow = TimeWindows.ofSizeAndGrace(Duration.ofSeconds(windowLengthSec), Duration.ofSeconds(windowAfterEndSec)).advanceBy(Duration.ofSeconds(windowShiftSec));

        KTable<Windowed<String>, Long> table = streamsBuilder.stream(topic, Consumed.with(stringSerde, stringSerde))
                .groupBy((key, value) -> value, Grouped.with(stringSerde, stringSerde))
                .windowedBy(hopWindow)
                .count();

        table.toStream().foreach(
                (window, valueCount) -> System.out.println("hop_window - " + window.window().start() + "/" + window.window().end() + ", value - " + window.key() + ", valueCount - " + valueCount)
        );

        Topology topology = streamsBuilder.build();
        System.out.println(topology.describe().toString());

        return new KafkaStreams(topology, new TablesProperties(groupId, tableCommitIntervalMs));
    }

}
