package com.egor.kafka.mappers;

import com.egor.kafka.dtos.TopicPartitionDTO;
import org.apache.kafka.common.TopicPartition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.Set;

@Mapper
public interface TopicPartitionMapper {

    @Mappings({
            @Mapping(target = "partition", expression = "java(topicPartition.partition())"),
            @Mapping(target = "topic", expression = "java(topicPartition.topic())")
    })
    TopicPartitionDTO map(TopicPartition topicPartition);

    Set<TopicPartitionDTO> map(Set<TopicPartition> topicPartitionSet);
}
