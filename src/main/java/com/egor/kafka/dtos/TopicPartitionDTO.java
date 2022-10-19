package com.egor.kafka.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopicPartitionDTO {

    private String topic;

    private int partition;

}
