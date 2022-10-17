package com.egor.kafka.consumers.executable;

import com.egor.kafka.consumers.StringConsumer;
import com.egor.kafka.properties.ConsumerProperties;

public class GamesConsumer {

    public static void main(String[] args) {
        var consumer = new StringConsumer(new ConsumerProperties("group", false, "earliest"));
        consumer.subscribe("games");
        while (true) {
            consumer.poll(1000);
        }
        //consumer.close();
    }

}
