package com.egor.si.controllers;

import com.egor.si.controllers.payload.request.GameRequest;
import com.egor.si.controllers.payload.response.GameResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("integration")
public class IntegrationController {

    @PostMapping
    public GameResponse createGame(@RequestBody GameRequest gameRequest) throws InterruptedException {
        log.info("post method: {}", gameRequest);
        Thread.sleep(7000);
        return new GameResponse(gameRequest.getName(), LocalDateTime.now());
    }

}
