package com.egor.kafka.consumers;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Properties;

@Slf4j
public class StringConsumer extends KafkaConsumer<String, String> {

    @Getter
    @Setter
    private String name;

    private Thread thread;

    private boolean enabled;


    private final Runnable runnable = () -> {

        while (enabled) {
            log.error("Start poll");

            ConsumerRecords<String, String> records = poll(Duration.ofMillis(1000));

            for (ConsumerRecord<String, String> record : records) {
                log.warn("partition={}, offset={}, key={}, value={}, timestmap={}", record.partition(), record.offset(), record.key(), record.value(), record.timestamp());
            }

            log.error("End poll");
        }

    };


    public void startPulling() {
        if (thread != null && thread.isAlive()) return;

        enabled = true;
        thread = new Thread(runnable);
        thread.start();

        log.info("Consumer {} started pulling...", name);
    }

    public void stopPulling() {
        if (thread == null) return;

        enabled = false;
        try {
            thread.join();

            log.info("Consumer {} stopped pulling...", name);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void close() {
        stopPulling();
        super.close();
        log.warn("Consumer {} closed !", name);
    }

//    public void seekAllPartitions(long offset) {
//        for (TopicPartition topicPartition : assignment()) {
//            seek(topicPartition, offset);
//        }
//    }
//
//    public void seekOnePartition(int partition, long offset) {
//        assignment().stream()
//                .filter(topicPartition -> topicPartition.partition() == partition)
//                .findFirst()
//                .ifPresent(topicPartition -> seek(topicPartition, offset));
//    }
//
//    public void assign(String topic, int... partitions) {
//        assign(
//                IntStream.of(partitions).mapToObj(partition -> new TopicPartition(topic, partition)).collect(Collectors.toSet())
//        );
//    }


    public StringConsumer(Properties properties) {
        super(properties);
    }

}
