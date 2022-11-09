package com.egor.kafka.processors;

import com.egor.kafka.punctuators.MultiTopicTrafficPunctuator;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.state.KeyValueStore;

import java.time.Duration;
import java.util.Arrays;

@RequiredArgsConstructor
public class MultiTopicTrafficProcessor extends AbstractProcessor<String, String> {

    private final String storeName;
    private final String[] topics;
    private KeyValueStore<String, Integer> store;

    @Override
    public void init(ProcessorContext context) {
        System.out.println(Arrays.toString(topics));

        super.init(context);
        store = context.getStateStore(storeName);

        context.schedule(
                Duration.ofSeconds(30),
                PunctuationType.WALL_CLOCK_TIME,
                new MultiTopicTrafficPunctuator(context, store, topics)
        );
    }

    @Override
    public void process(String key, String value) {

        String topic = context.topic();

        Integer nMessagesForTopic = store.get(topic);
        if (nMessagesForTopic == null) {
            nMessagesForTopic = 0;
        }
        store.put(topic, ++nMessagesForTopic);
    }
}
