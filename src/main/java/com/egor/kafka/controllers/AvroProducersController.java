package com.egor.kafka.controllers;

import com.egor.kafka.objects.Game;
import com.egor.kafka.properties.GameReflectionProducerProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("kafka/avro/producers")
public class AvroProducersController {

    private final Map<String, KafkaProducer<String, Game>> producers = new HashMap<>();

    private final Callback callback = new Callback() {
        @Override
        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
            if (e != null) {
                log.error("Send error!", e);
                return;
            }
            log.info("topic={}, partition={}, offset={}", recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset());
        }
    };


    @GetMapping
    public Set<String> getAll() {
        return producers.keySet();
    }

    @PostMapping("add")
    public void addProducer(@RequestParam String name) {
        producers.put(name, new KafkaProducer<>(new GameReflectionProducerProperties()));
    }

    @PostMapping("sendAsync")
    public void sendWithCallback(@RequestParam String name,
                                 @RequestParam String topic,
                                 @RequestParam int partition,
                                 @RequestBody Game value) {
        String key = null;
        producers.get(name).send(new ProducerRecord<>(topic, partition, key, value), callback);
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
    
}
