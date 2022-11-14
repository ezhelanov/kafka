package com.egor.kafka.controllers;

import com.egor.kafka.properties.ProducerProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("kafka/producers")
public class ProducersController {

    private final Map<String, KafkaProducer<String, String>> producers = new HashMap<>();

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
    public void addProducer(@RequestParam String name,
                            @RequestParam(defaultValue = "0") long lingerMs,
                            @RequestParam(defaultValue = "1") String acks,
                            @RequestParam(defaultValue = "16384") long batchSizeBytes) {
        producers.put(name, new KafkaProducer<>(new ProducerProperties(lingerMs, acks, batchSizeBytes)));
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
                                 @RequestParam(required = false) String key,
                                 @RequestParam String value) {
        producers.get(name).send(new ProducerRecord<>(topic, partition, key, value), callback);
    }

    @PostMapping("sendBatch")
    public void sendBatch(@RequestParam String name,
                          @RequestParam String topic,
                          @RequestParam int howMuch) {
        var producer = producers.get(name);
        String batchName = "BatchName" + new Date().getTime();
        for (int i = 0; i < howMuch; i++) {
            producer.send(new ProducerRecord<>(topic, 0, null, batchName + i), callback);
        }
    }

}
