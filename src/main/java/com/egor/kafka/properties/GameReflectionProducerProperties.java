package com.egor.kafka.properties;

import com.egor.kafka.serialization.GameReflectionSerializer;

public class GameReflectionProducerProperties extends ProducerProperties {

    public GameReflectionProducerProperties() {
        super();
    }

    @Override
    protected void putValueSerializer() {
        put("value.serializer", GameReflectionSerializer.class.getName());
    }
}
