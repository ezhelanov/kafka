package com.egor.kafka.consumers;

import com.egor.kafka.services.GroupService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class StringConsumer extends KafkaConsumer<String, String> {

    @Autowired
    private GroupService groupService;

    @Setter
    private boolean enabled;

    @Getter
    private final Thread thread = new Thread(() -> {

        while (enabled) {
            var messages = new ArrayList<String>();

            log.error("Start poll");
            for (var record : poll(Duration.ofMillis(1000))) {
                log.warn("partition={}, offset={}, key={}, value={}, timestmap={}", record.partition(), record.offset(), record.key(), record.value(), record.timestamp());
                messages.add(record.value());
            }
            log.error("End poll");

            groupService.getTotalReadMessages().addAll(messages);
        }

    });


    public void seekAllPartitions(long offset) {
        for (TopicPartition topicPartition : assignment()) {
            seek(topicPartition, offset);
        }
    }

    public void seekOnePartition(int partition, long offset) {
        assignment().stream()
                .filter(topicPartition -> topicPartition.partition() == partition)
                .findFirst()
                .ifPresent(topicPartition -> seek(topicPartition, offset));
    }

    public void assign(String topic, int... partitions) {
        assign(
                IntStream.of(partitions).mapToObj(partition -> new TopicPartition(topic, partition)).collect(Collectors.toSet())
        );
    }


    public StringConsumer(Properties properties) {
        super(properties);
    }
}
