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
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    public void deferOperatorTest() {
        Mono<Long> defer = Mono.defer(() -> Mono.just(System.currentTimeMillis()));

//        defer.subscribe(l  -> log.info("{}", l));
//        Thread.sleep(100);
//        defer.subscribe(l  -> log.info("{}", l));
//        Thread.sleep(100);
//        defer.subscribe(l  -> log.info("{}", l));
//        Thread.sleep(100);
//        defer.subscribe(l  -> log.info("{}", l));

        AtomicLong atomicLong = new AtomicLong();
        defer.subscribe(atomicLong::set);
        assertTrue(atomicLong.get() > 0);
    }

    @Test
    public void concatOperatorTest() {
        Flux<String> firstFlux = Flux.just("a", "b");
        Flux<String> secondFlux = Flux.just("c", "d");

        Flux<String> concatFlux = Flux.concat(firstFlux, secondFlux).log();

        StepVerifier.create(concatFlux)
                .expectSubscription()
                .expectNext("a","b","c","d")
                .verifyComplete();
    }

    @Test
    public void concatOperatorDelayErrorTest() {
        Flux<String> firstFlux = Flux.just("a", "b")
                .map(s -> {
                    if (s.equals("b")) {
                        throw new IllegalArgumentException();
                    }
                    return s;
                });
        Flux<String> secondFlux = Flux.just("c", "d");

        Flux<String> concatFlux = Flux.concatDelayError(firstFlux, secondFlux).log();

        StepVerifier.create(concatFlux)
                .expectSubscription()
                .expectNext("a", "c", "d")
                .expectError()
                .verify();
    }

    @Test
    public void concatWithOperatorTest() {
        Flux<String> firstFlux = Flux.just("a", "b");
        Flux<String> secondFlux = Flux.just("c", "d");

        Flux<String> concatFlux = firstFlux.concatWith(secondFlux).log();

        StepVerifier.create(concatFlux)
                .expectSubscription()
                .expectNext("a","b","c","d")
                .verifyComplete();
    }

    @Test
    public void combineLatestOperatorTest() {
        Flux<String> firstFlux = Flux.just("a", "b");
        Flux<String> secondFlux = Flux.just("c", "d");

        Flux<String> combineLatestFlux = Flux.combineLatest(firstFlux, secondFlux,
                (s1, s2) -> s1.toUpperCase() + s2.toUpperCase())
                .log();

        //there is no accuracy in this test
        StepVerifier.create(combineLatestFlux)
                .expectSubscription()
                .expectNext("BC","BD")
                .verifyComplete();
    }

    @Test
    public void mergeOperatorTest() {
        Flux<String> firstFlux = Flux.just("a", "b").delayElements(Duration.ofMillis(200));
        Flux<String> secondFlux = Flux.just("c", "d");

        Flux<String> mergedFlux = Flux.merge(firstFlux, secondFlux)
                .delayElements(Duration.ofMillis(200))
                .log();

        StepVerifier.create(mergedFlux)
                .expectSubscription()
                .expectNext("c","d", "a","b")
                .verifyComplete();
    }

    @Test
    public void mergeWithOperatorTest() {
        Flux<String> firstFlux = Flux.just("a", "b").delayElements(Duration.ofMillis(200));
        Flux<String> secondFlux = Flux.just("c", "d");

        Flux<String> mergedFlux = firstFlux.mergeWith(secondFlux).log();

        StepVerifier.create(mergedFlux)
                .expectSubscription()
                .expectNext("c","d", "a","b")
                .verifyComplete();
    }

    @Test
    public void mergeSequentialOperatorTest() {
        Flux<String> flux1 = Flux.just("a", "b").delayElements(Duration.ofMillis(200));
        Flux<String> flux2 = Flux.just("c", "d");

        Flux<String> mergedFlux = Flux.mergeSequential(flux1, flux2, flux1).log();

        StepVerifier.create(mergedFlux)
                .expectSubscription()
                .expectNext("a","b", "c","d", "a","b")
                .verifyComplete();
    }

}
