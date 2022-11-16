package com.egor.kafka.connectors;

import com.egor.kafka.tasks.EgorSourceTask;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EgorSourceConnector extends SourceConnector {

    @Override
    public Class<? extends Task> taskClass() {
        return EgorSourceTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {

        List<Map<String, String>> configs = new ArrayList<>();

        Map<String, String> config = new HashMap<>();
        // config.put("prop","val");
        configs.add(config);

        return configs;
    }

    @Override
    public ConfigDef config() {
        return new ConfigDef();
    }

    @Override
    public void start(Map<String, String> map) {}

    @Override
    public void stop() { }

    @Override
    public String version() { return "0.0.1-SNAPSHOT"; }
}
