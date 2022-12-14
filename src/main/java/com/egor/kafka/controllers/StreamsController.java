package com.egor.kafka.controllers;

import com.egor.kafka.mappers.StoreMapper;
import com.egor.kafka.services.StoresService;
import com.egor.kafka.utils.StreamsUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("kafka/streams")
public class StreamsController {

    private final Map<String, KafkaStreams> map = new HashMap<>();


    @Autowired
    private StoresService storesService;

    @Autowired
    private StreamsUtils streamsUtils;

    @Autowired
    private StoreMapper storeMapper;


    @PostMapping("simple")
    public void simple(@RequestParam String topic, @RequestParam String groupId) {
        map.put("simple", streamsUtils.simple(topic, groupId));
    }

    @PostMapping("predicate")
    public void predicate(@RequestParam String topic, @RequestParam String outTopic, @RequestParam String groupId) {
        map.put("predicate", streamsUtils.predicate(topic, outTopic, groupId));
    }

    @PostMapping("store")
    public void store(@RequestParam String topic, @RequestParam String groupId, @RequestParam String storeName) {
        map.put("store", streamsUtils.store(topic, groupId, storeName));
    }

    @PostMapping("store2")
    public void store2(@RequestParam String topic, @RequestParam String groupId, @RequestParam String storeName) {
        map.put("store2", streamsUtils.store(topic, groupId, storeName));
    }


    @GetMapping("{name}/state")
    public String getState(@PathVariable String name) {
        return map.get(name).state().name();
    }

    @PutMapping("{name}/start")
    public void start(@PathVariable String name) {
        map.get(name).start();
    }

    @PutMapping("{name}/close")
    public void close(@PathVariable String name) {
        map.get(name).close();
    }

    @DeleteMapping("{name}/delete")
    public void delete(@PathVariable String name) {
        map.remove(name);
    }

    @GetMapping("store")
    public Map<String, Object> store(@RequestParam(defaultValue = "store") String name,
                                     @RequestParam(defaultValue = "counts") String storeName) {
        return storeMapper.map(storesService.getStore(map.get(name), storeName));
    }

}
