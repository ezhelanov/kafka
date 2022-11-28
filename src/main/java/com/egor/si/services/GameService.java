package com.egor.si.services;

import com.egor.si.controllers.payload.response.GameResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GameService {

    public void handleResponse(GameResponse gameResponse) {
        log.info("RESPONSE HANDLED BY SERVICE - {}", gameResponse.getGameName());
    }

}
