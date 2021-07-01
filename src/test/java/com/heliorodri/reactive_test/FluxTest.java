package com.heliorodri.reactive_test;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxTest {

    private static final String ARGENTINA = "Argentina";
    private static final String BRAZIL = "Brazil";
    private static final String CANADA = "Canada";

    private static final List<String> COUNTRIES = Arrays.asList(ARGENTINA, BRAZIL, CANADA);

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

    @Test
    public void fluxSubscriberFromList(){
        Flux<String> countries = Flux.fromIterable(COUNTRIES);

        StepVerifier.create(countries).expectNext(ARGENTINA, BRAZIL, CANADA).verifyComplete();
    }

}
