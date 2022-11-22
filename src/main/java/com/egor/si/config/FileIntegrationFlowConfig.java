package com.egor.si.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.messaging.MessageHandler;

import java.io.File;
import java.time.Duration;

@Profile("file-integration-flow")
@Configuration
@EnableIntegration
public class FileIntegrationFlowConfig {

    @Bean
    public MessageSource<File> fileReadingMessageSource() {
        var sourceReader = new FileReadingMessageSource();
        sourceReader.setDirectory(new File("/home/egor/pics"));
        sourceReader.setFilter(new SimplePatternFileListFilter("*.png"));
        return sourceReader;
    }

    @Bean
    public MessageHandler fileWritingMessageHandler() {
        var handler = new FileWritingMessageHandler(new File("/home/egor/integration"));
        handler.setAutoCreateDirectory(true);
        handler.setFileExistsMode(FileExistsMode.IGNORE);
        handler.setExpectReply(false);
        return handler;
    }

    @Bean
    public IntegrationFlow fileFlow() {
        return IntegrationFlows
                .from(fileReadingMessageSource(), c -> c.poller(Pollers.fixedDelay(Duration.ofSeconds(5))))
                .handle(fileWritingMessageHandler())
                .get();
    }
}
