package com.egor.kafka.services;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Service
public class ConsumerGroupService {

    private final List<String> totalReadMessages = new ArrayList<>();

}
