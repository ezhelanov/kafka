package com.egor.kafka.serializers;

import com.egor.kafka.objects.Game;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
public class GameReflectionSerializer implements Serializer<Game> {

    @Override
    public byte[] serialize(String topic, Game data) {

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(baos, null);
            DatumWriter<Game> writer = new ReflectDatumWriter<>(Game.class); // без схемы
            writer.write(data, encoder);
            encoder.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new SerializationException(e.getMessage(), e);
        }
    }

}
