package com.egor.kafka.controllers;

import com.egor.kafka.producers.StringProducer;
import com.egor.kafka.properties.ProducerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("kafka/producers")
public class ProducersController {

    private final Map<String, StringProducer> producers = new HashMap<>();


    @PostMapping("producer")
    public void addProducer(@RequestParam String name){
        producers.put(name, new StringProducer(new ProducerProperties()));
    }

    @PutMapping("producer/close")
    public void close(@RequestParam String name){
        producers.get(name).close();
    }

    @GetMapping
    public Set<String> getAll(){
        return producers.keySet();
    }

    @PutMapping("closeAll")
    public void closeAll(){
        producers.forEach((name, producer) -> {
            producer.close();
            log.warn("-- closed producer -- {}", name);
        });
    }

    @DeleteMapping
    public void deleteAll(){
        producers.clear();
    }

    @PostMapping("producer/sendAsync")
    public void sendWithCallback(@RequestParam String name,
                                 @RequestParam String topic, @RequestParam int partition, @RequestParam String value) {
        producers.get(name).send(topic, partition, value);
    }

}
