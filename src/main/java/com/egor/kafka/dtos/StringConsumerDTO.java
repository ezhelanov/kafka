package com.egor.kafka.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class StringConsumerDTO {

    private Set<TopicPartitionDTO> assignment;

    private String groupId;

    private String name;
}
