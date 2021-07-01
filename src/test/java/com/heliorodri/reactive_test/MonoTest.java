package com.heliorodri.reactive_test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
public class MonoTest {

    private static final String SUPER_HERO_NAME = "Iron man";

    @Test
    public void monoSubscriberTest() {
        Mono<String> publisher = Mono.just(SUPER_HERO_NAME).log();

        StepVerifier.create(publisher).expectNext(SUPER_HERO_NAME).verifyComplete();
    }

    @Test
    public void monoSubscriberOverrideConsumerTest() {
        Mono<String> monoPublisher = Mono.just(SUPER_HERO_NAME);

        monoPublisher.subscribe(s -> log.info("The super is: {}", s));

        StepVerifier.create(monoPublisher).expectNext(SUPER_HERO_NAME).verifyComplete();
    }

    @Test
    public void monoSubscriberConsumerErrorTest() {
        Mono<String> monoPublisher = Mono.just(SUPER_HERO_NAME).map(s -> { throw new RuntimeException("testing error"); });

        monoPublisher.subscribe(
                hero -> log.info("The superHero is: {}", hero),
                error -> log.error(error.getMessage())
        );

        StepVerifier.create(monoPublisher).expectError(RuntimeException.class).verify();
    }

    @Test
    public void monoSubscriberConsumerCompleteAndLogTest() {
        Mono<String> monoPublisher = Mono.just(SUPER_HERO_NAME).map(String::toUpperCase);

        monoPublisher.subscribe(
                s -> log.info("The super is: {}", s),
                Throwable::printStackTrace,
                () -> log.info("FINISHED")
        );

        StepVerifier.create(monoPublisher).expectNext(SUPER_HERO_NAME.toUpperCase()).verifyComplete();
    }

    @Test
    public void monoSubscriberConsumerSubscriptionCancelTest() {
        Mono<String> monoPublisher = Mono.just(SUPER_HERO_NAME).map(String::toUpperCase);

        monoPublisher.subscribe(
                s -> log.info("The super is: {}", s),
                Throwable::printStackTrace,
                () -> log.info("FINISHED"),
                Subscription::cancel
        );
    }

    @Test
    public void monoDoOnMethodsTest() {
        Mono<String> monoPublisher = Mono.just(SUPER_HERO_NAME)
                .map(String::toUpperCase)
                .doOnSubscribe(subscription -> log.info("Subscribed"))
                .doOnRequest(l -> log.info("request received"))
                .doOnNext(s -> log.info("doOnNext-execution. Value {}", s))
                .doOnSuccess(s -> log.info("success: ALL DONE!"));

        monoPublisher.subscribe(
                s -> log.info("The super is: {}", s),
                Throwable::printStackTrace,
                () -> log.info("FINISHED")
        );
    }

}
