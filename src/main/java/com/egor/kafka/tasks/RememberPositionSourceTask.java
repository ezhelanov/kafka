package com.egor.kafka.tasks;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RememberPositionSourceTask extends SourceTask {

    private int count;

    @Override
    public void start(Map<String, String> props) {

        Map<String, Object> offsets = context.offsetStorageReader().offset(
                Collections.singletonMap("topic", "source-topic")
        );

        if (offsets != null) {
            Object lastOffset = offsets.get("position");
            if (lastOffset != null) {
                count = (int) lastOffset;
            }
        }
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {

        List<SourceRecord> records = new ArrayList<>();

        Thread.sleep(1000);

        records.add(new SourceRecord(
                Collections.singletonMap("topic", "source-topic"),
                Collections.singletonMap("position", count),
                "egor-topic",
                0,
                Schema.STRING_SCHEMA,
                "Hi,today from connect " + count++
        ));

        return records;
    }

    @Override
    public void stop() { }

    @Override
    public String version() { return "0.0.1-SNAPSHOT"; }

}
