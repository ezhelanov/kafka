package com.egor.si.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.core.GenericSelector;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.messaging.support.MessageBuilder;

import java.time.LocalDateTime;

@Slf4j
@Configuration
public class StringIntegrationConfig {

    @Bean
    public MessageSource<LocalDateTime> localDateTimeMessageSource() {
        return () -> MessageBuilder.withPayload(LocalDateTime.now()).build();
    }

    @Bean
    public IntegrationFlow flow() {
        return IntegrationFlows.from(localDateTimeMessageSource(), c -> c.poller(Pollers.fixedDelay(5000)))
                .transform(new GenericTransformer<LocalDateTime, String>() {
                    @Override
                    public String transform(LocalDateTime source) {
                        return "Minute - " + source.getMinute() + ", second - " + source.getSecond();
                    }
                })
                .filter(new GenericSelector<String>() {
                    @Override
                    public boolean accept(String source) {
                        return source.matches(".+[13579]");
                    }
                })
                .handle(message -> log.info(message.getPayload().toString()))
                .get();
    }

}
