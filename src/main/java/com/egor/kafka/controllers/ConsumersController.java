package com.egor.kafka.controllers;

import com.egor.kafka.consumers.StringConsumer;
import com.egor.kafka.consumers.StringConsumerFactory;
import com.egor.kafka.dtos.StringConsumerDTO;
import com.egor.kafka.mappers.StringConsumerMapper;
import lombok.extern.slf4j.Slf4j;
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
        log.info("Consumer {} started pulling...", name);
    }
    @PostMapping("startAll")
    public void startAll() {
        consumers.forEach((name, consumer) -> {
            consumer.startPulling();
            log.info("Consumer {} started pulling...", name);
        });
    }

    @PostMapping("stop")
    public void stop(@RequestParam String name) {
        consumers.get(name).stopPulling();
        log.info("Consumer {} stopped pulling...", name);
    }
    @PostMapping("stopAll")
    public void stopAll() {
        consumers.forEach((name, consumer) -> {
            consumer.stopPulling();
            log.info("Consumer {} stopped pulling...", name);
        });
    }

    @PutMapping("close")
    public void close(@RequestParam String name){
        var consumer = consumers.get(name);
        consumer.stopPulling();
        consumer.close();
    }
    @PutMapping("closeAll")
    public void closeAll() {
        stopAll();
        consumers.forEach((name, consumer) -> {
            consumer.close();
            log.warn("-- closed consumer -- {}", name);
        });
    }

    @PostMapping("add")
    public void addConsumer(@RequestParam String name,
                            @RequestParam String groupId,
                            @RequestParam(defaultValue = "true") boolean enableAutoCommit,
                            @RequestParam(defaultValue = "latest") String autoOffsetReset,
                            @RequestParam int autoCommitIntervalMs) {
        var consumer = stringConsumerFactory.get(groupId, enableAutoCommit, autoOffsetReset, autoCommitIntervalMs);
        consumer.setName(name);
        consumers.put(name, consumer);
    }

    @PatchMapping("subscribe")
    public void subscribe(@RequestParam String name,
                          @RequestParam String topic) {
        consumers.get(name).subscribe(Collections.singleton(topic));
    }

    @DeleteMapping
    public void deleteAll(){
        closeAll();
        consumers.clear();
    }


}
