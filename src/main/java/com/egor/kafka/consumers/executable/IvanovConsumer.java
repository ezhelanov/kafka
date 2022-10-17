package com.egor.kafka.consumers.executable;

import com.egor.kafka.consumers.StringConsumer;
import com.egor.kafka.properties.ConsumerProperties;

public class IvanovConsumer {
    public static void main(String[] args) {
        var consumer = new StringConsumer(new ConsumerProperties("ivanov", false, "earliest"));
        consumer.subscribe("lab1-duplicates");
        consumer.poll(1000);
        consumer.close();
    }
}
