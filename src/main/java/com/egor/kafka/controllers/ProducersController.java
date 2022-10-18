package com.egor.kafka.controllers;

import com.egor.kafka.producers.StringProducer;
import com.egor.kafka.properties.ProducerProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("kafka/producers")
public class ProducersController {

    private final Map<String, StringProducer> producers = new HashMap<>();

    @Autowired
    private Callback callback;


    @GetMapping
    public Set<String> getAll() {
        return producers.keySet();
    }

    @PostMapping("add")
    public void addProducer(@RequestParam String name) {
        producers.put(name, new StringProducer(new ProducerProperties()));
    }

    @PutMapping("close")
    public void close(@RequestParam String name) {
        producers.get(name).close();
    }

    @PutMapping("closeAll")
    public void closeAll() {
        producers.forEach((name, producer) -> {
            producer.close();
            log.warn("-- closed producer -- {}", name);
        });
    }

    @DeleteMapping
    public void deleteAll() {
        closeAll();
        producers.clear();
    }

    @PostMapping("sendAsync")
    public void sendWithCallback(@RequestParam String name,
                                 @RequestParam String topic,
                                 @RequestParam int partition,
                                 @RequestParam String value) {
        String key = null;
        producers.get(name).send(new ProducerRecord<>(topic, partition, key, value), callback);
    }

}
