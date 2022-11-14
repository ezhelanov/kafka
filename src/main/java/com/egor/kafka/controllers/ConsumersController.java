package com.egor.kafka.controllers;

import com.egor.kafka.consumers.StringConsumer;
import com.egor.kafka.dtos.StringConsumerDTO;
import com.egor.kafka.mappers.StringConsumerMapper;
import com.egor.kafka.services.StringConsumerService;
import com.egor.kafka.utils.StringConsumerUtils;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("kafka/consumers")
public class ConsumersController {

    private final Map<String, StringConsumer> consumers = new HashMap<>();


    @Autowired
    private StringConsumerUtils stringConsumerUtils;

    @Autowired
    private StringConsumerService stringConsumerService;

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
                            @RequestParam(defaultValue = "5000") int autoCommitIntervalMs,
                            @RequestParam(defaultValue = "500") int maxPollRecords,
                            @RequestParam(defaultValue = "1048576") int maxPartitionFetchBytes) {
        consumers.put(name, stringConsumerUtils.get(name, groupId, enableAutoCommit, autoOffsetReset, autoCommitIntervalMs, maxPollRecords, maxPartitionFetchBytes));
    }

    @PostMapping("addDuplicate")
    public void addDuplicatesConsumer(@RequestParam String name) {
        consumers.put(name, stringConsumerUtils.getDuplicates(name));
    }

    @PatchMapping("subscribe")
    public void subscribe(@RequestParam String name,
                          @RequestParam String topic) {
        consumers.get(name).subscribe(Collections.singleton(topic));
    }

    @PatchMapping("assignAll")
    public void assignAll(@RequestParam String name,
                          @RequestParam String topic,
                          @RequestParam(defaultValue = "3") int partitions) {
        stringConsumerService.assignAll(consumers.get(name), topic, partitions);
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
        stringConsumerService.singlePoll(consumers.get(name), duration);
    }

    @PostMapping("multiplePolls")
    public void multiplePolls(@RequestParam String name,
                              @RequestParam(defaultValue = "1000") long duration,
                              @RequestParam int howMuch) {
        for (int i = 0; i < howMuch; i++) singlePoll(name, duration);
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
