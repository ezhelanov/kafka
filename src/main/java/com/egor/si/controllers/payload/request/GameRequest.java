package com.egor.si.controllers.payload.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class GameRequest {

    private final String name;

    private final int year;

}
