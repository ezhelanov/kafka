package com.egor.kafka.controllers;

import com.egor.kafka.mappers.StringStoreMapper;
import com.egor.kafka.services.StreamsService;
import com.egor.kafka.utils.TablesUtils;
import com.egor.kafka.utils.WindowsUtils;
import org.apache.kafka.streams.KafkaStreams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

    @Autowired
    private WindowsUtils windowsUtils;


    @GetMapping
    public Set<String> getAll() {
        return map.keySet();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void add(@RequestParam String name,
                    @RequestParam String topic,
                    @RequestParam String groupId,
                    @RequestParam String storeName,
                    @RequestParam(defaultValue = "5000") long tableCommitIntervalMs) {
        map.put(name, tablesUtils.table(topic, groupId, storeName, tableCommitIntervalMs));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("addWindow")
    public void addWindow(@RequestParam String name,
                          @RequestParam String topic,
                          @RequestParam String groupId,
                          @RequestParam(defaultValue = "30000") long tableCommitIntervalMs,
                          @RequestParam(defaultValue = "5") long windowLengthSec,
                          @RequestParam(defaultValue = "2") long windowShiftSec,
                          @RequestParam(defaultValue = "1800") long windowAfterEndSec) {
        switch (name) {
            case "session" -> map.put(name, windowsUtils.session(topic, groupId, tableCommitIntervalMs, windowLengthSec, windowAfterEndSec));
            case "tumble" -> map.put(name, windowsUtils.tumble(topic, groupId, tableCommitIntervalMs, windowLengthSec, windowAfterEndSec));
            case "hop" -> map.put(name, windowsUtils.hop(topic, groupId, tableCommitIntervalMs, windowLengthSec, windowShiftSec, windowAfterEndSec));
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("addKeyDuplicatesCount")
    public void addWindow(@RequestParam String name,
                          @RequestParam String topic,
                          @RequestParam String groupId,
                          @RequestParam(defaultValue = "30000") long tableCommitIntervalMs) {
        map.put(name, windowsUtils.keyDuplicatesCount(topic, groupId, tableCommitIntervalMs));
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
