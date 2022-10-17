package com.egor.kafka.consumers.executable;

import com.egor.kafka.consumers.StringConsumer;
import com.egor.kafka.properties.ConsumerProperties;

public class RequestConsumer2 {
    public static void main(String[] args) {
        var consumer = new StringConsumer(new ConsumerProperties("requests", false, "earliest"));
        consumer.subscribe("requests");
        consumer.poll(1000);
        consumer.poll(1000);
        //consumer.poll(1000);
        //consumer.poll(1000);
        consumer.close();
    }
}
