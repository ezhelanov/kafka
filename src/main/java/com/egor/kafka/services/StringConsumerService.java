package com.egor.kafka.services;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class StringConsumerService {

    @Getter
    private final List<String> totalReadMessages = new ArrayList<>();


    public List<String> poll(KafkaConsumer<String, String> consumer, long millis) {
        var messages = new ArrayList<String>();

        log.debug("Start poll");
        for (var record : consumer.poll(Duration.ofMillis(millis))) {
            log.trace("partition={}, offset={}, key={}, value={}, timestmap={}", record.partition(), record.offset(), record.key(), record.value(), record.timestamp());
            messages.add(record.value());
        }
        log.debug("End poll");

        return messages;
    }


//    public void seekAllPartitions(KafkaConsumer<String, String> consumer, long offset) {
//        for (TopicPartition topicPartition : consumer.assignment()) {
//            consumer.seek(topicPartition, offset);
//        }
//    }
//
//    public void seekOnePartition(KafkaConsumer<String, String> consumer, int partition, long offset) {
//        consumer.assignment().stream()
//                .filter(topicPartition -> topicPartition.partition() == partition)
//                .findFirst()
//                .ifPresent(topicPartition -> consumer.seek(topicPartition, offset));
//    }
//
//    public void assign(KafkaConsumer<String, String> consumer, String topic, int...partitions) {
//        consumer.assign(
//                IntStream.of(partitions).mapToObj(partition -> new TopicPartition(topic, partition)).collect(Collectors.toSet())
//        );
//    }
}
