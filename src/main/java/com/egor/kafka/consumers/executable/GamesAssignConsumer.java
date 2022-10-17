package com.egor.kafka.consumers.executable;

import com.egor.kafka.consumers.StringConsumer;
import com.egor.kafka.properties.ConsumerProperties;

public class GamesAssignConsumer {
    public static void main(String[] args) {
        var consumer = new StringConsumer(new ConsumerProperties());
        consumer.assign("games_part", 0, 2);
        consumer.seekAll(13);
        consumer.poll(1000);
        consumer.poll(1000);
        consumer.commitSync();
        consumer.close();
    }
}
