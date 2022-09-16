package com.egor.kafka;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class KafkaApplication {

    @Autowired
    private Producer<String, String> producer;


    public static void main(String[] args) {
        SpringApplication.run(KafkaApplication.class, args);
    }


    @EventListener(ContextRefreshedEvent.class)
    public void doSomething(ContextRefreshedEvent event) {

        String topic = "games";
        int partition = 0;
        String key = "K";
        String value = "Tomy Lee Jones";

        for (int i = 0; i < 3; i++) {
            producer.send(new ProducerRecord<>(
                    topic, partition, key + i, value
            ));
        }
        producer.close();
    }

}
