package com.egor.kafka.controllers;

import com.egor.kafka.consumers.StringConsumer;
import com.egor.kafka.consumers.StringConsumerFactory;
import com.egor.kafka.dtos.StringConsumerDTO;
import com.egor.kafka.mappers.StringConsumerMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public Set<StringConsumerDTO> getAll(){
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
    public void close(@RequestParam String name){
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
    public void deleteAll(){
        closeAll();
        consumers.clear();
    }
    @DeleteMapping("delete")
    public void delete(@RequestParam String name) {
        consumers.get(name).close();
        consumers.remove(name);
    }


}
