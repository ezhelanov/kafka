package com.egor.kafka.consumers.executable;

import com.egor.kafka.consumers.StringConsumer;
import com.egor.kafka.properties.ConsumerProperties;

public class GamesPartConsumer1 {

    public static void main(String[] args)throws InterruptedException {
        var consumer = new StringConsumer(
                new ConsumerProperties("consumer_group", false, "earliest")
        );
        consumer.subscribe("games_part");
        consumer.poll(1000);
        consumer.seekConcrete(2, 0);
        consumer.poll(1000);
        consumer.poll(1000);
        consumer.close();
    }

}
