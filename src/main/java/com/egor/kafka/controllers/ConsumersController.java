package com.egor.kafka.controllers;

import com.egor.kafka.consumers.StringConsumer;
import com.egor.kafka.controllers.payload.response.StringConsumerDTO;
import com.egor.kafka.properties.ConsumerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("kafka/consumers")
public class ConsumersController {

    private final Map<String, StringConsumer> consumers = new HashMap<>();


    @GetMapping
    public Map<String, StringConsumerDTO> getAll(){
        var map = new HashMap<String, StringConsumerDTO>();
        consumers.forEach((k,v) -> map.put(k, v.toDTO()));
        return map;
    }

    @PostMapping("add")
    public void addConsumer(@RequestParam String name,
                            @RequestParam String groupId,
                            @RequestParam(defaultValue = "true") boolean enableAutoCommit,
                            @RequestParam(defaultValue = "latest") String autoOffsetReset,
                            @RequestParam int autoCommitIntervalMs){
        consumers.put(name, new StringConsumer(new ConsumerProperties(groupId, enableAutoCommit, autoOffsetReset, autoCommitIntervalMs)));
    }

    @PatchMapping("subscribe")
    public void subscribe(@RequestParam String name,
                          @RequestParam String topic) {
        consumers.get(name).subscribe(topic);
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
        consumers.clear();
    }


}
