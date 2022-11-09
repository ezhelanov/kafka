package com.egor.kafka.punctuators;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.Punctuator;
import org.apache.kafka.streams.state.KeyValueStore;

@RequiredArgsConstructor
public class TrafficPunctuator implements Punctuator {

    private final ProcessorContext context;
    private final KeyValueStore<String, Integer> store;

    @Override
    public void punctuate(long timestamp) {

        Integer nMessages = store.get("traffic");
        if (nMessages == null) {
            nMessages = 0;
        }
        store.put("traffic", 0);

        context.forward(null, "got " + nMessages + " messages");
    }

}
