package com.egor.kafka.objects;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Game {

    private int id;

    private String name;

    private String type;

}
