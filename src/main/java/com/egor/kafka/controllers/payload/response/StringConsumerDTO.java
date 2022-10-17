package com.egor.kafka.controllers.payload.response;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.common.TopicPartition;

import java.util.Set;

@Getter
@Setter
public class StringConsumerDTO {

    private String groupId;

    private Set<TopicPartition> assignment;
}
