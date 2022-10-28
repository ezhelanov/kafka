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

    protected boolean enabled;

    protected Runnable runnable;


    protected void initRunnable() {
        runnable = () -> {

            while (enabled) {
                log.error("Start poll");

                ConsumerRecords<String, String> records = poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, String> record : records) {
                    log.warn("partition={}, offset={}, key={}, value={}, timestamp={}", record.partition(), record.offset(), record.key(), record.value(), record.timestamp());
                }

                log.error("End poll");
            }

        };
    }


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


    public StringConsumer(Properties properties) {
        super(properties);
        initRunnable();
    }
}
