package com.egor.kafka.utils;

import com.egor.kafka.processors.MultiTopicTrafficProcessor;
import com.egor.kafka.processors.TrafficProcessor;
import com.egor.kafka.processors.UpperCaseProcessor;
import com.egor.kafka.properties.ProcessorApiProperties;
import org.apache.kafka.common.serialization.*;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.processor.UsePartitionTimeOnInvalidTimestamp;
import org.apache.kafka.streams.state.Stores;
import org.springframework.stereotype.Component;

@Component
public class ProcessorApiUtils {

    public KafkaStreams upperCase(String groupId,
                                  String stringSourceTopic,
                                  String stringSinkTopic,
                                  String intSinkTopic,
                                  String stringUpperCaseNodeName,
                                  String stringSourceNodeName,
                                  String stringSinkNodeName,
                                  String intSinkNodeName) {

        Deserializer<String> stringDeserializer = new StringDeserializer();
        Serializer<String> stringSerializer = new StringSerializer();

        Topology topology = new Topology();

        topology.addSource(
                Topology.AutoOffsetReset.EARLIEST,
                stringSourceNodeName,
                new UsePartitionTimeOnInvalidTimestamp(),
                stringDeserializer,
                stringDeserializer,
                stringSourceTopic
        ).addProcessor(
                stringUpperCaseNodeName,
                () -> new UpperCaseProcessor(stringSinkNodeName, intSinkNodeName),
                stringSourceNodeName
        ).addSink(
                stringSinkNodeName,
                stringSinkTopic,
                stringSerializer,
                stringSerializer,
                stringUpperCaseNodeName
        ).addSink(
                intSinkNodeName,
                intSinkTopic,
                stringSerializer,
                stringSerializer,
                stringUpperCaseNodeName
        );

        System.out.println(topology.describe().toString());

        return new KafkaStreams(topology, new ProcessorApiProperties(groupId));
    }

    public KafkaStreams trafficPunctuator(String groupId,
                                          String sourceTopic,
                                          String storeName,
                                          String trafficSinkTopic,
                                          String trafficSinkNodeName,
                                          String trafficNodeName,
                                          String sourceNodeName) {

        Deserializer<String> stringDeserializer = new StringDeserializer();
        Serializer<String> stringSerializer = new StringSerializer();

        Topology topology = new Topology();

        topology.addSource(
                Topology.AutoOffsetReset.LATEST,
                sourceNodeName,
                stringDeserializer,
                stringDeserializer,
                sourceTopic
        ).addProcessor(
                trafficNodeName,
                () -> new TrafficProcessor(storeName),
                sourceNodeName
        ).addStateStore(
                Stores.keyValueStoreBuilder(
                        Stores.inMemoryKeyValueStore(storeName), Serdes.String(), Serdes.Integer()
                ),
                trafficNodeName
        ).addSink(
                trafficSinkNodeName,
                trafficSinkTopic,
                stringSerializer,
                stringSerializer,
                trafficNodeName
        );

        System.out.println(topology.describe().toString());

        return new KafkaStreams(topology, new ProcessorApiProperties(groupId));
    }

    public KafkaStreams multiTopics(String groupId,
                                    String sourceNodeName,
                                    String storeName,
                                    String trafficNodeName,
                                    String trafficSinkNodeName,
                                    String trafficSinkTopic,
                                    String... sourceTopics) {

        Deserializer<String> stringDeserializer = new StringDeserializer();
        Serializer<String> stringSerializer = new StringSerializer();

        Topology topology = new Topology();

        topology.addSource(
                Topology.AutoOffsetReset.LATEST,
                sourceNodeName,
                stringDeserializer,
                stringDeserializer,
                sourceTopics
        ).addProcessor(
                trafficNodeName,
                () -> new MultiTopicTrafficProcessor(storeName, sourceTopics),
                sourceNodeName
        ).addSink(
                trafficSinkNodeName,
                trafficSinkTopic,
                stringSerializer,
                stringSerializer,
                trafficNodeName
        ).addStateStore(
                Stores.keyValueStoreBuilder(
                        Stores.inMemoryKeyValueStore(storeName), Serdes.String(), Serdes.Integer()
                ),
                trafficNodeName
        );

        System.out.println(topology.describe().toString());

        return new KafkaStreams(topology, new ProcessorApiProperties(groupId));
    }

}
