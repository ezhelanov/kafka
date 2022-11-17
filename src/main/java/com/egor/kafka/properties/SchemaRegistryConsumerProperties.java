package com.egor.kafka.properties;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;

public class SchemaRegistryConsumerProperties extends ConsumerProperties {

    {
        put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
    }

    public SchemaRegistryConsumerProperties() {
        super();
    }

    public SchemaRegistryConsumerProperties(String groupId, boolean enableAutoCommit, String autoOffsetReset, int autoCommitIntervalMs) {
        super(groupId, enableAutoCommit, autoOffsetReset, autoCommitIntervalMs);
    }

    @Override
    protected void putValueDeserializer() {
        put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
    }


}
