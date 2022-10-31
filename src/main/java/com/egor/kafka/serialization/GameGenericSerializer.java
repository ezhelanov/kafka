package com.egor.kafka.serialization;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class GameGenericSerializer implements Serializer<GenericRecord> {

    @Override
    public byte[] serialize(String topic, GenericRecord data) {

        try {
            Schema schema = new Schema.Parser().parse(
                    getClass().getClassLoader().getResourceAsStream("game.avsc")
            );
            DatumWriter<GenericRecord> writer = new GenericDatumWriter<>(schema);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(baos, null);
            writer.write(data, encoder);
            encoder.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new SerializationException(e.getMessage(), e);
        }

    }

}
