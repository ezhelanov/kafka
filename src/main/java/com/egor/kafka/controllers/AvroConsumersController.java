package com.egor.kafka.controllers;

import com.egor.kafka.objects.Game;
import com.egor.kafka.properties.GameReflectionConsumerProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("kafka/avro/consumers")
public class AvroConsumersController {

    private final Map<String, KafkaConsumer<String, Game>> consumers = new HashMap<>();


    @PostMapping("add")
    public void addConsumer(@RequestParam String name,
                            @RequestParam(required = false) String groupId,
                            @RequestParam(defaultValue = "true") boolean enableAutoCommit,
                            @RequestParam(defaultValue = "latest") String autoOffsetReset,
                            @RequestParam(defaultValue = "0") int autoCommitIntervalMs) {
        if (groupId == null) {
            consumers.put(name, new KafkaConsumer<>(new GameReflectionConsumerProperties()));
            return;
        }
        consumers.put(name, new KafkaConsumer<>(new GameReflectionConsumerProperties(groupId, enableAutoCommit, autoOffsetReset, autoCommitIntervalMs)));
    }

    @PatchMapping("subscribe")
    public void subscribe(@RequestParam String name,
                          @RequestParam String topic) {
        consumers.get(name).subscribe(Collections.singleton(topic));
    }

    @PatchMapping("assignAll")
    public void assignAll(@RequestParam String name,
                          @RequestParam String topic) {
        consumers.get(name).assign(Set.of(
                new TopicPartition(topic, 0),
                new TopicPartition(topic, 1),
                new TopicPartition(topic, 2)
        ));
    }

    @PostMapping("singlePoll")
    public void singlePoll(@RequestParam String name,
                           @RequestParam(defaultValue = "1000") long duration) {
        log.error("Start poll");

        var records = consumers.get(name).poll(Duration.ofMillis(duration));
        for (var record : records) {
            log.warn("partition={}, offset={}, key={}, value={}, timestamp={}", record.partition(), record.offset(), record.key(), record.value(), record.timestamp());
        }

        log.error("End poll");
    }

    @PostMapping("seek")
    public void seek(@RequestParam String name,
                     @RequestParam String topic,
                     @RequestParam int partition,
                     @RequestParam(defaultValue = "0") long offset) {
        consumers.get(name).seek(new TopicPartition(topic, partition), offset);
    }

    @PostMapping("seekAll")
    public void seekAll(@RequestParam String name,
                        @RequestParam(defaultValue = "0") long offset) {
        var consumer = consumers.get(name);
        consumer.assignment().forEach(topicPartition -> consumer.seek(topicPartition, offset));
    }

    @DeleteMapping
    public void deleteAll() {
        consumers.values().forEach(KafkaConsumer::close);
        consumers.clear();
    }

}
