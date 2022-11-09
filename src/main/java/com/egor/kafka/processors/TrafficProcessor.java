package com.egor.kafka.processors;

import com.egor.kafka.punctuators.TrafficPunctuator;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.state.KeyValueStore;

import java.time.Duration;

@RequiredArgsConstructor
public class TrafficProcessor extends AbstractProcessor<String, String> {

    private final String storeName;
    private KeyValueStore<String, Integer> store;

    @Override
    public void process(String key, String value) {

        Integer nMessages = store.get("traffic");
        if (nMessages == null) {
            nMessages = 0;
        }
        store.put("traffic", ++nMessages);
    }

    @Override
    public void init(ProcessorContext context) {
        super.init(context);
        store = context.getStateStore(storeName);

        TrafficPunctuator punctuator = new TrafficPunctuator(context, store);
        context.schedule(
                Duration.ofSeconds(2),
                PunctuationType.WALL_CLOCK_TIME,
                punctuator
        );
    }
}
