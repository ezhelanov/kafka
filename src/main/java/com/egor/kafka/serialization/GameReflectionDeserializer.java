package com.egor.kafka.serialization;

import com.egor.kafka.objects.Game;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

public class GameReflectionDeserializer implements Deserializer<Game> {

    @Override
    public Game deserialize(String topic, byte[] data) {

        try {
            BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(data, null);
            DatumReader<Game> reader = new ReflectDatumReader<>(Game.class);
            return reader.read(null, decoder);
        } catch (IOException e) {
            throw new SerializationException(e.getMessage(), e);
        }

    }
}
