package com.egor.kafka.producers.executable;

import com.egor.kafka.producers.StringProducer;
import com.egor.kafka.properties.ProducerProperties;

public class RequestProducer {

    public static void main(String[] args) throws InterruptedException {
        var producer = new StringProducer(new ProducerProperties());
        producer.send("requests", 2,"ineeeeeeeeeeeesurer=Zhelanov;model=Some;other text");
        producer.send("requests", 0,"ineeeeeeeeeeesurer=Ivanov;model=Some;other text");
        producer.close();
    }

}
