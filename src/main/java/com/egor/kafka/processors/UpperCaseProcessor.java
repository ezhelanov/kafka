package com.egor.kafka.processors;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.To;

@RequiredArgsConstructor
public class UpperCaseProcessor extends AbstractProcessor<String, String> {

    private final String stringNode;
    private final String intNode;

    @Override
    public void process(String key, String value) {
        try {
            Integer.parseInt(value);
            context.forward(key, value, To.child(intNode));
        } catch (NumberFormatException e) {
            context.forward(key, value, To.child(stringNode));
            context.forward(key, value.toUpperCase(), To.child(stringNode));
        }
    }
}
