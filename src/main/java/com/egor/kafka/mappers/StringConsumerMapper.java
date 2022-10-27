package com.egor.kafka.mappers;

import com.egor.kafka.consumers.StringConsumer;
import com.egor.kafka.dtos.StringConsumerDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.InvalidGroupIdException;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Set;

@Slf4j
@Mapper
public abstract class StringConsumerMapper {

    @Autowired
    private TopicPartitionMapper topicPartitionMapper;


    protected StringConsumerDTO map(StringConsumer consumer) {
        var dto = new StringConsumerDTO();
        dto.setAssignment(topicPartitionMapper.map(consumer.assignment()));
        dto.setName(consumer.getName());
        dto.setSubscriptions(consumer.subscription());
        try {
            dto.setGroupId(consumer.groupMetadata().groupId());
        } catch (InvalidGroupIdException e) {
            log.warn("No 'group.id' defined in consumer '{}'", dto.getName());
        }
        return dto;
    }

    public abstract Set<StringConsumerDTO> map(Collection<StringConsumer> stringConsumerCollection);

}
