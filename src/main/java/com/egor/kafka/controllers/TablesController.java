package com.egor.kafka.controllers;

import com.egor.kafka.mappers.StoreMapper;
import com.egor.kafka.services.StreamsService;
import com.egor.kafka.utils.ProcessorApiUtils;
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
    private StoreMapper storeMapper;

    @Autowired
    private WindowsUtils windowsUtils;

    @Autowired
    private ProcessorApiUtils processorApiUtils;


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

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("addUpperCase")
    public void upperCase(@RequestParam String name,
                          @RequestParam String groupId,
                          @RequestParam(defaultValue = "games") String stringSourceTopic,
                          @RequestParam(defaultValue = "games_string") String stringSinkTopic,
                          @RequestParam(defaultValue = "games_int") String intSinkTopic,
                          @RequestParam(defaultValue = "uppercase_node") String stringUpperCaseNodeName,
                          @RequestParam(defaultValue = "string_source_node") String stringSourceNodeName,
                          @RequestParam(defaultValue = "string_sink_node") String stringSinkNodeName,
                          @RequestParam(defaultValue = "int_sink_node") String intSinkNodeName) {
        map.put(name, processorApiUtils.upperCase(groupId, stringSourceTopic, stringSinkTopic, intSinkTopic, stringUpperCaseNodeName, stringSourceNodeName, stringSinkNodeName, intSinkNodeName));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("addTrafficPunctuator")
    public void trafficPunctuator(@RequestParam String name,
                                  @RequestParam String groupId,
                                  @RequestParam(defaultValue = "games") String sourceTopic,
                                  @RequestParam(defaultValue = "source_node") String sourceNodeName,
                                  @RequestParam(defaultValue = "traffic_store") String storeName,
                                  @RequestParam(defaultValue = "traffic_topic") String trafficSinkTopic,
                                  @RequestParam(defaultValue = "traffic_sink_node") String trafficSinkNodeName,
                                  @RequestParam(defaultValue = "traffic_node") String trafficNodeName) {
        map.put(name, processorApiUtils.trafficPunctuator(groupId, sourceTopic, storeName, trafficSinkTopic, trafficSinkNodeName, trafficNodeName, sourceNodeName));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("addMulti")
    public void multiTopics(@RequestParam String name,
                                    @RequestParam String groupId,
                                    @RequestParam(defaultValue = "source_node") String sourceNodeName,
                                    @RequestParam(defaultValue = "traffic_store") String storeName,
                                    @RequestParam(defaultValue = "traffic_node") String trafficNodeName,
                                    @RequestParam(defaultValue = "traffic_sink_node") String trafficSinkNodeName,
                                    @RequestParam(defaultValue = "traffic_topic") String trafficSinkTopic,
                                    @RequestParam String sourceTopics) {
        map.put(name, processorApiUtils.multiTopics(groupId, sourceNodeName, storeName, trafficNodeName, trafficSinkNodeName, trafficSinkTopic, sourceTopics.split(",")));
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
    public Map<String, Object> store(@RequestParam String name,
                                     @RequestParam String storeName) {
        return storeMapper.map(streamsService.getStore(map.get(name), storeName));
    }


}
