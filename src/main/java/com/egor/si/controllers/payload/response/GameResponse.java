package com.egor.si.controllers.payload.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameResponse {

    private String gameName;

    private LocalDateTime creationTime;

}
