package com.egor.kafka.punctuators;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.Punctuator;
import org.apache.kafka.streams.state.KeyValueStore;

@RequiredArgsConstructor
public class MultiTopicTrafficPunctuator implements Punctuator {

    private final ProcessorContext context;
    private final KeyValueStore<String, Integer> store;
    private final String[] topics;

    @Override
    public void punctuate(long timestamp) {

        for (String topic : topics) {

            Integer nMessagesForTopic = store.get(topic);
            if (nMessagesForTopic == null || nMessagesForTopic == 0) {
                context.forward(null, topic);
            }
            store.put(topic, 0);
        }

    }
}
