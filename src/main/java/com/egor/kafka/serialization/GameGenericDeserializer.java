package com.egor.kafka.serialization;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.File;
import java.io.IOException;

public class GameGenericDeserializer implements Deserializer<GenericRecord> {

    @Override
    public GenericRecord deserialize(String topic, byte[] data) {

        try {
            Schema schema = new Schema.Parser().parse(
                    getClass().getClassLoader().getResourceAsStream("game.avsc")
            );
            DatumReader<GenericRecord> reader = new GenericDatumReader<>(schema);
            BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(data, null);
            return reader.read(null, decoder);
        } catch (IOException e) {
            throw new SerializationException(e.getMessage(), e);
        }

    }

}
