package com.egor.kafka.controllers;

import com.egor.kafka.objects.Game;
import com.egor.kafka.properties.GameGenericConsumerProperties;
import com.egor.kafka.properties.GameReflectionConsumerProperties;
import com.egor.kafka.properties.SchemaRegistryConsumerProperties;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("kafka/avro/consumers")
public class AvroConsumersController {

    private final Map<String, KafkaConsumer<String, Game>> consumers = new HashMap<>();
    private final Map<String, KafkaConsumer<String, GenericRecord>> consumersGeneric = new HashMap<>();

    @GetMapping("reflections")
    public Set<String> getAll() {
        return consumers.keySet();
    }

    @GetMapping("generics")
    public Set<String> getAllGenerics() {
        return consumersGeneric.keySet();
    }

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

    @PostMapping("addGeneric")
    public void addConsumer2(@RequestParam String name,
                             @RequestParam(required = false) String groupId,
                             @RequestParam(defaultValue = "true") boolean enableAutoCommit,
                             @RequestParam(defaultValue = "latest") String autoOffsetReset,
                             @RequestParam(defaultValue = "0") int autoCommitIntervalMs) {
        if (groupId == null) {
            consumersGeneric.put(name, new KafkaConsumer<>(new GameGenericConsumerProperties()));
            return;
        }
        consumersGeneric.put(name, new KafkaConsumer<>(new GameGenericConsumerProperties(groupId, enableAutoCommit, autoOffsetReset, autoCommitIntervalMs)));
    }

    @ApiOperation(value = "Create consumer with SchemaRegistry")
    @PostMapping("addGeneric2")
    public void addGeneric2(@RequestParam String name,
                            @RequestParam(required = false) String groupId,
                            @RequestParam(defaultValue = "true") boolean enableAutoCommit,
                            @RequestParam(defaultValue = "latest") String autoOffsetReset,
                            @RequestParam(defaultValue = "0") int autoCommitIntervalMs) {
        if (groupId == null) {
            consumersGeneric.put(name, new KafkaConsumer<>(new SchemaRegistryConsumerProperties()));
            return;
        }
        consumersGeneric.put(name, new KafkaConsumer<>(new SchemaRegistryConsumerProperties(groupId, enableAutoCommit, autoOffsetReset, autoCommitIntervalMs)));
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

    @PatchMapping("assignAllGeneric")
    public void assignAll2(@RequestParam String name,
                           @RequestParam String topic) {
        consumersGeneric.get(name).assign(Set.of(
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

    @PostMapping("singlePollGeneric")
    public void singlePoll2(@RequestParam String name,
                            @RequestParam(defaultValue = "1000") long duration) {
        log.error("Start poll");

        var records = consumersGeneric.get(name).poll(Duration.ofMillis(duration));
        for (var record : records) {
            log.warn("partition={}, offset={}, key={}, value={}, timestamp={}", record.partition(), record.offset(), record.key(), record.value(), record.timestamp());
        }

        log.error("End poll");
    }


    @PostMapping("seekAll")
    public void seekAll(@RequestParam String name,
                        @RequestParam(defaultValue = "0") long offset) {
        var consumer = consumers.get(name);
        consumer.assignment().forEach(topicPartition -> consumer.seek(topicPartition, offset));
    }

    @PostMapping("seekAllGeneric")
    public void seekAll2(@RequestParam String name,
                         @RequestParam(defaultValue = "0") long offset) {
        var consumer = consumersGeneric.get(name);
        consumer.assignment().forEach(topicPartition -> consumer.seek(topicPartition, offset));
    }

    @DeleteMapping
    public void deleteAll() {
        consumers.values().forEach(KafkaConsumer::close);
        consumers.clear();
        consumersGeneric.values().forEach(KafkaConsumer::close);
        consumersGeneric.clear();
    }

}
