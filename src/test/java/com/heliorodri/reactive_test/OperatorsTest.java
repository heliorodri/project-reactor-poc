package com.heliorodri.reactive_test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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

    @Test
    public void publishOnSimpleTest(){
        Flux<Integer> flux = Flux.range(1,5)
                .map(i -> {
                    log.info("Map1 : value {} on Thread {}", i, Thread.currentThread().getName());
                    return i;
                })
                .publishOn(Schedulers.boundedElastic())
                .map(i -> {
                    log.info("Map2 : value {} on Thread {}", i, Thread.currentThread().getName());
                    return i;
                });

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext(1,2,3,4,5)
                .verifyComplete();
    }

    @Test
    public void multiSubscribeOnSimpleTest(){
        Flux<Integer> flux = Flux.range(1,5)
                .subscribeOn(Schedulers.single())
                .map(i -> {
                    log.info("Map1 : value {} on Thread {}", i, Thread.currentThread().getName());
                    return i;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(i -> {
                    log.info("Map2 : value {} on Thread {}", i, Thread.currentThread().getName());
                    return i;
                });

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext(1,2,3,4,5)
                .verifyComplete();
    }

    @Test
    public void multiPublishOnSimpleTest(){
        Flux<Integer> flux = Flux.range(1,5)
                .publishOn(Schedulers.single())
                .map(i -> {
                    log.info("Map1 : value {} on Thread {}", i, Thread.currentThread().getName());
                    return i;
                })
                .publishOn(Schedulers.boundedElastic())
                .map(i -> {
                    log.info("Map2 : value {} on Thread {}", i, Thread.currentThread().getName());
                    return i;
                });

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext(1,2,3,4,5)
                .verifyComplete();
    }

    @Test
    public void publishAndSubscribeOnSimpleTest(){
        Flux<Integer> flux = Flux.range(1,5)
                .publishOn(Schedulers.single())
                .map(i -> {
                    log.info("Map1 : value {} on Thread {}", i, Thread.currentThread().getName());
                    return i;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(i -> {
                    log.info("Map2 : value {} on Thread {}", i, Thread.currentThread().getName());
                    return i;
                });

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext(1,2,3,4,5)
                .verifyComplete();
    }

    @Test
    public void subscribeAndPublishOnSimpleTest(){
        Flux<Integer> flux = Flux.range(1,5)
                .subscribeOn(Schedulers.single())
                .map(i -> {
                    log.info("Map1 : value {} on Thread {}", i, Thread.currentThread().getName());
                    return i;
                })
                .publishOn(Schedulers.boundedElastic())
                .map(i -> {
                    log.info("Map2 : value {} on Thread {}", i, Thread.currentThread().getName());
                    return i;
                });

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext(1,2,3,4,5)
                .verifyComplete();
    }

    @Test
    public void subscribeOnIOTest() {
        Path file = Paths.get("text-file");

         Mono<List<String>> list = Mono.fromCallable(() -> Files.readAllLines(file))
                .log()
                .subscribeOn(Schedulers.boundedElastic());

         StepVerifier.create(list)
                 .expectSubscription()
                 .thenConsumeWhile(strings -> {
                     Assertions.assertFalse(strings.isEmpty());
                     log.info("list size: {}", strings.size());
                     return true;
                 })
         .verifyComplete();
    }

    @Test
    public void switchIfEmptyOperatorTest(){
        String notEmptyString = "This is not empty anymore";
        Flux<Object> emptyFlux = Flux.empty();

        Flux<Object> flux = emptyFlux.switchIfEmpty(Flux.just(notEmptyString)).log();

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext(notEmptyString)
                .verifyComplete();
    }

}
