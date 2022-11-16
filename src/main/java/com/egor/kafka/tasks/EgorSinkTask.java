package com.egor.kafka.tasks;

import com.egor.kafka.connectors.EgorSinkConnector;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

@Slf4j
public class EgorSinkTask extends SinkTask {

    private int count;

    private String taskNumber;
    private String fileName;


    @Override
    public void start(Map<String, String> props) {
        taskNumber = props.get(EgorSinkConnector.NAME_CONFIG);
        fileName = props.get(EgorSinkConnector.FILE_CONFIG);
    }

    @Override
    public void put(Collection<SinkRecord> sinkRecords) {
        count += sinkRecords.size();
    }

    @Override
    public void flush(Map<TopicPartition, OffsetAndMetadata> currentOffsets) {

        try (var bw = new BufferedWriter(new FileWriter(fileName, true))) {

            bw.write(taskNumber + ": count=" + count + "\n");

        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        count = 0;
    }

    @Override
    public void stop() { }

    @Override
    public String version() { return "0.0.1-SNAPSHOT"; }
}
