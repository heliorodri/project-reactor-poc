package com.heliorodri.reactive_test;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxTest {

    private static final String ARGENTINA = "Argentina";
    private static final String BRAZIL = "Brazil";
    private static final String CANADA = "Canada";

    @Test
    public void fluxSubscriber(){
        Flux<String> fluxCountries = Flux.just(ARGENTINA, BRAZIL, CANADA).log();

        StepVerifier.create(fluxCountries).expectNext(ARGENTINA, BRAZIL, CANADA).verifyComplete();
    }

    @Test
    public void fluxSubscriberNumbers(){
        Flux<Integer> fluxStarsRate = Flux.range(1,5);

        StepVerifier.create(fluxStarsRate).expectNext(1,2,3,4,5).verifyComplete();
    }

}
