package com.egor.kafka.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Slf4j
@Component
public class StreamsService {

    public void startAndCloseStream(Topology topology, Properties properties) {

        KafkaStreams kafkaStreams = new KafkaStreams(topology, properties);

        log.info("Streaming app started!");
        kafkaStreams.start();
        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        kafkaStreams.close();
        log.info("Streaming app closed!");

        kafkaStreams.cleanUp(); // очищаем хранилище
    }

}
