package com.egor.kafka.controllers;

import com.egor.kafka.consumers.StringConsumer;
import com.egor.kafka.consumers.StringConsumerFactory;
import com.egor.kafka.dtos.StringConsumerDTO;
import com.egor.kafka.mappers.StringConsumerMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("kafka/consumers")
public class ConsumersController {

    private final Map<String, StringConsumer> consumers = new HashMap<>();


    @Autowired
    private StringConsumerFactory stringConsumerFactory;

    @Autowired
    private StringConsumerMapper mapper;


    @GetMapping
    public Set<StringConsumerDTO> getAll() {
        consumers.values().forEach(StringConsumer::stopPulling);
        return mapper.map(consumers.values());
    }


    @PostMapping("start")
    public void start(@RequestParam String name) {
        consumers.get(name).startPulling();
    }

    @PostMapping("startAll")
    public void startAll() {
        consumers.values().forEach(StringConsumer::startPulling);
    }

    @PostMapping("stop")
    public void stop(@RequestParam String name) {
        consumers.get(name).stopPulling();
    }

    @PostMapping("stopAll")
    public void stopAll() {
        consumers.values().forEach(StringConsumer::stopPulling);
    }

    @PutMapping("close")
    public void close(@RequestParam String name) {
        consumers.get(name).close();
    }

    @PutMapping("closeAll")
    public void closeAll() {
        consumers.values().forEach(StringConsumer::close);
    }

    @PostMapping("add")
    public void addConsumer(@RequestParam String name,
                            @RequestParam(required = false) String groupId,
                            @RequestParam(defaultValue = "true") boolean enableAutoCommit,
                            @RequestParam(defaultValue = "latest") String autoOffsetReset,
                            @RequestParam(defaultValue = "0") int autoCommitIntervalMs) {
        var consumer = stringConsumerFactory.get(groupId, enableAutoCommit, autoOffsetReset, autoCommitIntervalMs);
        consumer.setName(name);
        consumers.put(name, consumer);
    }

    @PostMapping("addDuplicate")
    public void addDuplicatesConsumer(@RequestParam String name) {
        var duplicatesConsumer = stringConsumerFactory.get();
        duplicatesConsumer.setName(name);
        consumers.put(name, duplicatesConsumer);
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

    @DeleteMapping
    public void deleteAll() {
        closeAll();
        consumers.clear();
    }

    @DeleteMapping("delete")
    public void delete(@RequestParam String name) {
        consumers.get(name).close();
        consumers.remove(name);
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


}
