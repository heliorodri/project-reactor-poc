package com.heliorodri.reactive_test.operator;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class Movie {

    private String title;
    private String mainCharacter;

}
