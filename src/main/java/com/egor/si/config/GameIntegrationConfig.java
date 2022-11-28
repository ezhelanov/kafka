package com.egor.si.config;

import com.egor.si.controllers.payload.request.GameRequest;
import com.egor.si.controllers.payload.response.GameResponse;
import com.egor.si.services.GameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.http.dsl.Http;
import org.springframework.messaging.support.MessageBuilder;

import java.time.LocalDateTime;

@Slf4j
@Configuration
public class GameIntegrationConfig {

    @Bean
    public MessageSource<GameRequest> gameRequestMessageSource() {
        return () -> {
            var now = LocalDateTime.now();
            return MessageBuilder.withPayload(new GameRequest("SomeGame " + now.getNano(), now.getYear()))
                    .build();
        };
    }

    @Bean
    public IntegrationFlow flow(GameService gameService) {
        return IntegrationFlows.from(gameRequestMessageSource(), c -> c.poller(Pollers.fixedDelay(3000)))
                .handle(Http.outboundGateway("http://localhost:1998/integration")
                        .httpMethod(HttpMethod.POST)
                        .expectedResponseType(GameResponse.class)
                )
                .handle(gameService)
                .get();
    }

}
