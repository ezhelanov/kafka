package com.egor.kafka.consumers.executable;

import com.egor.kafka.consumers.StringConsumer;
import com.egor.kafka.properties.ConsumerProperties;

public class RequestConsumer {
    public static void main(String[] args) {
        var consumer = new StringConsumer(new ConsumerProperties("requests", true, "earliest"));
        consumer.subscribe("requests");
        consumer.poll(1000);
        consumer.seekAll(0);
        consumer.poll(1000);
        consumer.seekAll(0);
        consumer.poll(1000);
        consumer.close();
    }
}
