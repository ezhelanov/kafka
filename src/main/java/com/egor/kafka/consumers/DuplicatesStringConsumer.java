package com.egor.kafka.consumers;

import com.egor.kafka.properties.ConsumerProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.Properties;

@Slf4j
public class DuplicatesStringConsumer extends StringConsumer {

    @Override
    protected void initRunnable() {
        runnable = () -> {

            var duplicatesConsumer = new KafkaConsumer<>(new ConsumerProperties());

            while (enabled) {
                log.error("Start poll");

                ConsumerRecords<String, String> records = poll(Duration.ofMillis(1000));

                duplicatesConsumer.assign(assignment());

                for (ConsumerRecord<String, String> record : records) {

                    if (record.offset() == 0) {
                        log.warn("PREVIOUS: undefined");
                        log.warn("partition={}, offset={}, key={}, value={}, timestamp={}", record.partition(), record.offset(), record.key(), record.value(), record.timestamp());
                        continue;
                    }

                    duplicatesConsumer.seek(new TopicPartition(record.topic(), record.partition()), record.offset() - 1);
                    var previousRecord = duplicatesConsumer.poll(Duration.ofMillis(1000)).iterator().next();

                    log.warn("PREVIOUS: partition={}, offset={}, key={}, value={}, timestamp={}", previousRecord.partition(), previousRecord.offset(), previousRecord.key(), previousRecord.value(), previousRecord.timestamp());
                    log.warn("partition={}, offset={}, key={}, value={}, timestamp={}", record.partition(), record.offset(), record.key(), record.value(), record.timestamp());
                }

                log.error("End poll");
            }

            duplicatesConsumer.close();
        };
    }

    public DuplicatesStringConsumer(Properties properties) { super(properties); }
}
