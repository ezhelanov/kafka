package com.egor.kafka.mappers;

import com.egor.kafka.consumers.StringConsumer;
import com.egor.kafka.dtos.StringConsumerDTO;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Set;

@Mapper
public abstract class StringConsumerMapper {

    @Autowired
    private TopicPartitionMapper topicPartitionMapper;

    protected StringConsumerDTO map(StringConsumer consumer) {
        var dto = new StringConsumerDTO();
        dto.setAssignment(topicPartitionMapper.map(consumer.assignment()));
        dto.setName(consumer.getName());
        dto.setSubscriptions(consumer.subscription());
        dto.setGroupId(consumer.groupMetadata() == null ? null : consumer.groupMetadata().groupId());
        return dto;
    }

    public abstract Set<StringConsumerDTO> map(Collection<StringConsumer> stringConsumerCollection);

}
