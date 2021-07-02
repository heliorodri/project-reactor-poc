package com.heliorodri.reactive_test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

@Slf4j
public class OperatorsTest {

    @Test
    public void subscribeOnSimpleTest(){
        Flux<Integer> flux = Flux.range(1,5)
                .map(i -> {
                    log.info("Map1 : value {} on Thread {}", i, Thread.currentThread().getName());
                    return i;
                })
                .subscribeOn(Schedulers.single())
                .map(i -> {
                    log.info("Map2 : value {} on Thread {}", i, Thread.currentThread().getName());
                    return i;
                });


        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext(1,2,3,4,5)
                .verifyComplete();

    }

}
