package com.egor.kafka.controllers;

import com.egor.kafka.objects.Game;
import com.egor.kafka.properties.GameGenericProducerProperties;
import com.egor.kafka.properties.GameReflectionProducerProperties;
import com.egor.kafka.properties.SchemaRegistryProducerProperties;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("kafka/avro/producers")
public class AvroProducersController {

    private final Map<String, KafkaProducer<String, Game>> reflections = new HashMap<>();
    private final Map<String, KafkaProducer<String, GenericRecord>> generics = new HashMap<>();

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


    @GetMapping("reflections")
    public Set<String> getAll() {
        return reflections.keySet();
    }

    @GetMapping("generics")
    public Set<String> getAllGenerics() {
        return generics.keySet();
    }

    @PostMapping("add/reflection")
    public void addProducer(@RequestParam String name) {
        reflections.put(name, new KafkaProducer<>(new GameReflectionProducerProperties()));
    }

    @PostMapping("add/generic")
    public void addProducer2(@RequestParam String name,
                             @RequestParam(defaultValue = "false") boolean isWithSchemaRegistry) {
        if (isWithSchemaRegistry)
            generics.put(name, new KafkaProducer<>(new SchemaRegistryProducerProperties()));
        else
            generics.put(name, new KafkaProducer<>(new GameGenericProducerProperties()));
    }

    @PostMapping("sendAsync/reflection")
    public void sendWithCallback(@RequestParam String name,
                                 @RequestParam String topic,
                                 @RequestParam int partition,
                                 @RequestBody Game value) {
        reflections.get(name).send(new ProducerRecord<>(topic, partition, null, value), callback);
    }

    @PostMapping("sendAsync/generic")
    public void sendWithCallback2(@RequestParam String name,
                                  @RequestParam String topic,
                                  @RequestParam int partition,
                                  @ApiParam(value = "Avro Schema file name") @RequestParam(defaultValue = "game.avsc") String schemaName,
                                  @RequestBody Game value) throws IOException {

        Schema schema = new Schema.Parser().parse(getClass().getClassLoader().getResourceAsStream(schemaName));

        GenericRecord genericRecord = new GenericData.Record(schema);
        genericRecord.put("id", value.getId());
        genericRecord.put("name", value.getName());
        if (value.getType() != null) {
            genericRecord.put("type", value.getType());
        }
        if (value.getCompany() != null) {
            genericRecord.put("company", value.getCompany());
        }

        generics.get(name).send(new ProducerRecord<>(topic, partition, null, genericRecord), callback);
    }

    @PutMapping("closeAll")
    public void closeAll() {
        reflections.forEach((name, producer) -> {
            producer.close();
            log.warn("-- closed producer -- {}", name);
        });
        generics.forEach((name, producer) -> {
            producer.close();
            log.warn("-- closed producer -- {}", name);
        });
    }

    @DeleteMapping
    public void deleteAll() {
        closeAll();
        reflections.clear();
        generics.clear();
    }

}
