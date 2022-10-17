package com.egor.kafka.consumers.executable;

import com.egor.kafka.consumers.StringConsumer;
import com.egor.kafka.properties.ConsumerProperties;

public class GamesPartConsumer2 {

    public static void main(String[] args) {
        var consumer = new StringConsumer(new ConsumerProperties("consumer_group", false, "earliest"));
        consumer.subscribe("games_part");
        while (true) {
            consumer.poll(5000);
        }
    }

}
