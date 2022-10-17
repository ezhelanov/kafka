package com.egor.kafka.consumers;

import com.egor.kafka.controllers.payload.response.StringConsumerDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Slf4j
public class StringConsumer {

    private final Consumer<String, String> consumer;


    public void subscribe(String topic) {
        consumer.subscribe(Collections.singleton(topic));
    }

    public void close(){
        consumer.close();
    }

    public void poll(long millis) {
        log.debug("Start poll");
        var records = consumer.poll(Duration.ofMillis(millis));
        for (var record : records) {
            log.trace("partition={}, offset={}, key={}, value={}, timestmap={}", record.partition(), record.offset(), record.key(), record.value(), record.timestamp());
        }
        log.debug("End poll");
    }

    public void seekAll(long offset) {
        for (TopicPartition topicPartition : consumer.assignment()) {
            consumer.seek(topicPartition, offset);
        }
    }

    public void seekConcrete(int partition, long offset) {
        consumer.assignment().stream()
                .filter(topicPartition -> topicPartition.partition() == partition)
                .findFirst()
                .ifPresent(topicPartition -> consumer.seek(topicPartition, offset));
    }

    public void assign(String topic, int...partitions) {
        consumer.assign(
                IntStream.of(partitions).mapToObj(partition -> new TopicPartition(topic, partition)).collect(Collectors.toSet())
        );
    }

    public void commitSync(){
        consumer.commitSync();
    }

    public StringConsumerDTO toDTO() {
        var dto = new StringConsumerDTO();
        dto.setGroupId(consumer.groupMetadata().groupId());
        dto.setAssignment(consumer.assignment());
        return dto;
    }


    public StringConsumer(Properties properties) {
        this.consumer = new KafkaConsumer<>(properties);
    }
}
