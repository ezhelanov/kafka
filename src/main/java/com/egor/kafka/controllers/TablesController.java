package com.egor.kafka.controllers;

import com.egor.kafka.services.StreamsService;
import com.egor.kafka.utils.TablesUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
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


    @PostMapping
    public void table(@RequestParam String topic, @RequestParam String groupId, @RequestParam String storeName){
        map.put("table", tablesUtils.table(topic, groupId, storeName));
    }


    @GetMapping("{name}/state")
    public String getState(@PathVariable String name){
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

    @GetMapping("{name}/store")
    public void store(@PathVariable String name, @RequestParam String storeName){
        streamsService.printCountsStore(map.get(name), storeName);
    }

}
