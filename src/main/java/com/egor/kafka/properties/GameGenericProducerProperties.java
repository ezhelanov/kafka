package com.egor.kafka.properties;

import com.egor.kafka.serialization.GameGenericSerializer;

public class GameGenericProducerProperties extends ProducerProperties {

    public GameGenericProducerProperties() {
        super();
    }

    @Override
    protected void putValueSerializer() {
        put("value.serializer", GameGenericSerializer.class.getName());
    }
}
