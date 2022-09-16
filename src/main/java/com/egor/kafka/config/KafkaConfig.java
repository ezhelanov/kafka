package com.egor.kafka.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KafkaConfig {

    @Bean
    public Properties kafkaProperties(){
        Properties properties = new Properties();

        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer"); // ключ -> массив байт
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer"); // сообщение -> массив байт

        return properties;
    }

}
