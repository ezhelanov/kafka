package com.egor.kafka.controllers;

import com.egor.kafka.mappers.StringStoreMapper;
import com.egor.kafka.services.StreamsService;
import com.egor.kafka.utils.TablesUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("kafka/tables")
public class TablesController {

    private final Map<String, KafkaStreams> map = new HashMap<>();


    @Autowired
    private TablesUtils tablesUtils;

    @Autowired
    private StreamsService streamsService;

    @Autowired
    private StringStoreMapper mapper;


    @PostMapping
    public void add(@RequestParam String name,
                    @RequestParam String topic,
                    @RequestParam String groupId,
                    @RequestParam String storeName,
                    @RequestParam(defaultValue = "5000") long tableCommitIntervalMs) {
        map.put(name, tablesUtils.table(topic, groupId, storeName, tableCommitIntervalMs));
    }

    @DeleteMapping
    public void delete(@RequestParam String name) {
        close(name);
        map.remove(name);
    }

    @GetMapping("state")
    public String getState(@RequestParam String name) {
        return map.get(name).state().name();
    }

    @PutMapping("start")
    public void start(@RequestParam String name) {
        map.get(name).start();
    }

    @PutMapping("close")
    public void close(@RequestParam String name) {
        map.get(name).close();
    }

    @GetMapping("store")
    public Map<String, String> store(@RequestParam String name,
                                     @RequestParam String storeName) {
        return mapper.map(streamsService.getStringStore(map.get(name), storeName));
    }


}
