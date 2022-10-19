package com.egor.kafka.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@JsonPropertyOrder({"name", "groupId", "subscriptions", "assignment"})
public class StringConsumerDTO {

    private Set<TopicPartitionDTO> assignment;

    private String groupId;

    private String name;

    private Set<String> subscriptions;
}
