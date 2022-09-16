package com.egor.kafka.services.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Properties;

@Component
public class ProducerService {

    @Resource
    private Properties kafkaProperties;


    public void pushToOnePartitionTopic() {

        var simpleProducer = new KafkaProducer<String, String>(kafkaProperties); // старт потока

        var topic = "games";
        var partition = 0;
        var key = "K";
        var value = "Tomy Lee Jones";

        for (int i = 0; i < 3; i++) {
            simpleProducer.send(new ProducerRecord<>(topic, partition, key + i, value));
        }

        simpleProducer.close(); // закрываем поток

    }
}
