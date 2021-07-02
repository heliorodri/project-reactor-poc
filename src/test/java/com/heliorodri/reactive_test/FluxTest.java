package com.heliorodri.reactive_test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class FluxTest {

    private static final String ARGENTINA = "Argentina";
    private static final String BRAZIL = "Brazil";
    private static final String CANADA = "Canada";

    private static final List<String> COUNTRIES = Arrays.asList(ARGENTINA, BRAZIL, CANADA);

    @Test
    public void fluxSubscriberTest(){
        Flux<String> fluxCountries = Flux.just(ARGENTINA, BRAZIL, CANADA).log();

        StepVerifier.create(fluxCountries).expectNext(ARGENTINA, BRAZIL, CANADA).verifyComplete();
    }

    @Test
    public void fluxSubscriberNumbersTest(){
        Flux<Integer> fluxStarsRate = Flux.range(1,5);

        StepVerifier.create(fluxStarsRate).expectNext(1,2,3,4,5).verifyComplete();
    }

    @Test
    public void fluxSubscriberFromListTest(){
        Flux<String> countries = Flux.fromIterable(COUNTRIES);

        StepVerifier.create(countries).expectNext(ARGENTINA, BRAZIL, CANADA).verifyComplete();
    }

    @Test
    public void fluxSubscriberCountriesErrorTest(){
        Flux<String> countries = Flux.fromIterable(COUNTRIES)
                .map(country -> {
                    if (!country.equals(ARGENTINA)) {
                        throw new IllegalArgumentException("Messi can only play for " + ARGENTINA);
                    }
                    return country;
                })
                .doOnSubscribe(s -> log.info("Country: {}", s))
                .doOnError(Throwable::printStackTrace);


        StepVerifier.create(countries)
                .expectNext(ARGENTINA)
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    public void fluxSubscriberBackPressureTest(){
        long limitRequest = 3L;

        Flux<Integer> flux = Flux.range(1,5)
                .log()
                .doOnSubscribe(s -> log.info("Value: {}", s))
                .doOnError(Throwable::printStackTrace)
                .take(limitRequest);

        StepVerifier.create(flux).expectNext(1,2,3).verifyComplete();
    }

    @Test
    public void fluxSubscriberBackPressureWithLimitRateTest(){
        Flux<Integer> flux = Flux.range(1,10)
                .log()
                .limitRate(2);

        StepVerifier.create(flux)
                .expectNext(1,2)
                .expectNext(3,4)
                .expectNext(5,6)
                .expectNext(7,8)
                .expectNext(9,10)
                .verifyComplete();
    }

    @Test
    public void fluxSubscriberTestingIntervalWithVirtualTimeTest() {
        //Interval runs in a parallel thread
        StepVerifier.withVirtualTime(() -> Flux.interval(Duration.ofDays(1)).log())
                .expectSubscription()
                .expectNoEvent(Duration.ofHours(24))
                .thenAwait(Duration.ofDays(2))
                .expectNext(0L)
                .expectNext(1L)
                .thenCancel()
                .verify();
    }

    @Test
    public void connectableFluxTest() {
        ConnectableFlux<Integer> connectableFlux = Flux.range(1,10)
                .delayElements(Duration.ofMillis(100))
                .publish();

        StepVerifier.create(connectableFlux)
                .then(connectableFlux::connect)
                .expectNext(1,2,3,4,5,6,7,8,9,10)
                .expectComplete()
                .verify();

    }

    @Test
    public void connectableFluxAutoConnectTest() {
        Flux<Integer> fluxAutoCon = Flux.range(1,5)
                .delayElements(Duration.ofMillis(100))
                .publish()
                .autoConnect(3);

        StepVerifier.create(fluxAutoCon)
                .then(fluxAutoCon::subscribe)
                .then(fluxAutoCon::subscribe)
                .expectNext(1,2,3,4,5)
                .expectComplete()
                .verify();

    }

}
