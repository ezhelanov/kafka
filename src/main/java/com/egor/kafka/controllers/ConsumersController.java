package com.egor.kafka.controllers;

import com.egor.kafka.consumers.StringConsumer;
import com.egor.kafka.consumers.StringConsumerFactory;
import com.egor.kafka.services.ConsumerGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("kafka/consumers")
public class ConsumersController {

    private final Map<String, StringConsumer> consumers = new HashMap<>();


    @Autowired
    private StringConsumerFactory stringConsumerFactory;

    @Autowired
    private ConsumerGroupService consumerGroupService;


    @GetMapping
    public Set<String> getAll(){
        return consumers.keySet();
    }

    @GetMapping("messages")
    public List<String> getTotalReadMessages(){
        return consumerGroupService.getTotalReadMessages();
    }

    @PostMapping("start")
    public void startPulling(@RequestParam String name) {
        consumers.get(name).startPulling();
        log.info("Consumer {} started pulling...", name);
    }

    @PostMapping("stop")
    public void stopPulling(@RequestParam String name) throws InterruptedException {
        consumers.get(name).stopPulling();
        log.info("Consumer {} stopped pulling...", name);
    }

    @PostMapping("add")
    public void addConsumer(@RequestParam String name,
                            @RequestParam String groupId,
                            @RequestParam(defaultValue = "true") boolean enableAutoCommit,
                            @RequestParam(defaultValue = "latest") String autoOffsetReset,
                            @RequestParam int autoCommitIntervalMs){
        consumers.put(name, stringConsumerFactory.get(groupId, enableAutoCommit, autoOffsetReset, autoCommitIntervalMs));
    }

    @PatchMapping("subscribe")
    public void subscribe(@RequestParam String name,
                          @RequestParam String topic) {
        consumers.get(name).subscribe(Collections.singleton(topic));
    }

    @PutMapping("close")
    public void close(@RequestParam String name){
        consumers.get(name).close();
    }

    @PutMapping("closeAll")
    public void closeAll(){
        consumers.forEach((name, consumer) -> {
            consumer.close();
            log.warn("-- closed consumer -- {}", name);
        });
    }

    @DeleteMapping
    public void deleteAll(){
        closeAll();
        consumers.clear();
    }


}
