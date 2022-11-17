package com.egor.kafka.connectors;

import com.egor.kafka.tasks.EgorSinkTask;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EgorSinkConnector extends SinkConnector {

    public static final String FILE_CONFIG = "file";
    public static final String NAME_CONFIG = "name";

    private static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(FILE_CONFIG,
                    ConfigDef.Type.STRING,
                    ConfigDef.Importance.HIGH,
                    "Destination filename");


    private String fileName;


    @Override
    public void start(Map<String, String> props) {

        AbstractConfig parsedConfig = new AbstractConfig(CONFIG_DEF, props);

        fileName = parsedConfig.getString(FILE_CONFIG);
    }

    @Override
    public Class<? extends Task> taskClass() {
        return EgorSinkTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        List<Map<String, String>> configs = new ArrayList<>();

        for (int i = 0; i < maxTasks; i++) {

            Map<String, String> config = new HashMap<>();
            config.put(NAME_CONFIG, "EGOR-NUMBER-" + (i + 1));
            config.put(FILE_CONFIG, fileName);
            configs.add(config);
        }

        return configs;
    }

    @Override
    public ConfigDef config() {
        return new ConfigDef();
    }

    @Override
    public void stop() { }

    @Override
    public String version() { return "0.0.1-SNAPSHOT"; }
}
