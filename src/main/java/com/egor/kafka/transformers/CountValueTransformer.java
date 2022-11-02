package com.egor.kafka.transformers;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

@RequiredArgsConstructor
public class CountValueTransformer implements ValueTransformer<String, String> {

    private final String storeName;

    private KeyValueStore<String, Integer> store;

    private ProcessorContext processorContext;


    @Override
    public void init(ProcessorContext processorContext) {
        this.processorContext = processorContext;
        this.store = this.processorContext.getStateStore(storeName);
    }

    @Override
    public String transform(String value) {

        Integer count = store.get(value);
        if (count == null) {
            count = 0;
        }
        count++;
        store.put(value, count);

        return String.format("value - '%s', count - %d", value, count);
    }

    @Override
    public void close() {

    }
}
