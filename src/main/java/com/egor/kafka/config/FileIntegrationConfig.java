package com.egor.kafka.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.io.File;

@Configuration
@EnableIntegration
public class FileIntegrationConfig {

    @Bean
    @InboundChannelAdapter(
            value = "fileChannel",
            poller = @Poller(fixedDelay = "5000")
    )
    public MessageSource<File> fileReadingMessageSource() {
        var sourceReader = new FileReadingMessageSource();
        sourceReader.setDirectory(new File("/home/egor/pics"));
        sourceReader.setFilter(new SimplePatternFileListFilter("*.png"));
        return sourceReader;
    }

    @Bean
    public MessageChannel fileChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "fileChannel")
    public MessageHandler fileWritingMessageHandler() {
        var handler = new FileWritingMessageHandler(new File("/home/egor/integration"));
        handler.setAutoCreateDirectory(true);
        handler.setFileExistsMode(FileExistsMode.IGNORE);
        handler.setExpectReply(false);
        return handler;
    }

}
