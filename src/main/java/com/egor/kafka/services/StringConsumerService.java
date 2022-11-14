package com.egor.kafka.services;

import com.egor.kafka.consumers.StringConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Component
public class StringConsumerService {

    public void singlePoll(StringConsumer consumer, long duration) {
        log.error("Start poll");

        var records = consumer.poll(Duration.ofMillis(duration));
        System.out.println("received " + records.count() + " records");
        for (var record : records) {
            log.warn("partition={}, offset={}, key={}, value={}, timestamp={}", record.partition(), record.offset(), record.key(), record.value(), record.timestamp());
        }

        log.error("End poll");
    }

    public void assignAll(StringConsumer consumer, String topic, int partitions) {
        consumer.assign(
                IntStream.range(0, partitions)
                        .mapToObj(partition -> new TopicPartition(topic, partition))
                        .collect(Collectors.toUnmodifiableSet())
        );
    }
}
