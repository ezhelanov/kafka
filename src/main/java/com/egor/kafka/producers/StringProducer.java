package com.egor.kafka.producers;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.*;

import java.util.Properties;

@Slf4j
public class StringProducer {

    private final Producer<String, String> producer;


    private final Callback callback = new Callback() {
        @Override
        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
            if (e != null) {
                log.error("Send error!", e);
                return;
            }
            log.info("topic={}, partition={}, offset={}", recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset());
        }
    };


    public void send(String topic, int partition, String value) {
        producer.send(new ProducerRecord<>(topic, partition, "Key" + partition * 1000, value), callback);
    }

    public void close() {
        producer.close();
    }


    public StringProducer(Properties properties) {
        this.producer = new KafkaProducer<>(properties);
    }

}
