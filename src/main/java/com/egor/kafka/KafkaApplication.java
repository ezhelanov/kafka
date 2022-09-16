package com.egor.kafka;

import com.egor.kafka.services.producer.ProducerService;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import javax.annotation.Resource;
import java.util.Properties;

@SpringBootApplication
public class KafkaApplication {

    @Autowired
    private ProducerService producerService;


    public static void main(String[] args) {
        SpringApplication.run(KafkaApplication.class, args);
    }


    @EventListener(ContextRefreshedEvent.class)
    public void doSomething(ContextRefreshedEvent event) {
        producerService.pushToOnePartitionTopic();
    }

}
