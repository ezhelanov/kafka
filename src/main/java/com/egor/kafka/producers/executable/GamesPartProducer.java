package com.egor.kafka.producers.executable;

import com.egor.kafka.producers.StringProducer;
import com.egor.kafka.properties.ProducerProperties;

public class GamesPartProducer {

    public static void main(String[] args) throws InterruptedException {
        var producer = new StringProducer(new ProducerProperties());
        producer.send("games_part", 0, "I am in 0 partition");
        producer.send("games_part", 1, "I am in 1 partition");
        producer.send("games_part", 2, "I am in 2 partition");
        producer.close();
        Thread.sleep(5000);
    }

}
