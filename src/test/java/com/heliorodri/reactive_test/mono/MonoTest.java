package com.heliorodri.reactive_test.mono;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MonoTest {

    private static final String SUPER_HERO_NAME = "Iron man";

    @BeforeAll
    public static void setUp(){
        BlockHound.install();
    }

    @Test
    public void blockHoundWorks() {
        try {
            FutureTask<?> task = new FutureTask<>(() -> {
                Thread.sleep(0);
                return "";
            });
            Schedulers.parallel().schedule(task);

            task.get(10, TimeUnit.SECONDS);
            Assertions.fail("should fail");
        } catch (Exception e) {
            Assertions.assertTrue(e.getCause() instanceof BlockingOperationError);
        }
    }

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

    @Test
    public void monoDoOnErrorMethodTest() {
        Mono<Object> error = Mono.error(new RuntimeException("Testing doOnError method"))
                .doOnSubscribe(subscription -> log.info("Subscribed"))
                .doOnRequest(request -> log.info("request received"))
                .doOnNext(value -> log.info("doOnNext-execution. Value {}", value))
                .doOnSuccess(success -> log.info("success: ALL DONE!"))
                .doOnError(Throwable::printStackTrace);

        StepVerifier.create(error).expectError(RuntimeException.class).verify();
    }

    @Test
    public void monoOnErrorResumeTest() {
        String noSuperHero = "";

        Mono<Object> error = Mono.error(new RuntimeException("Testing doOnError method"))
                .doOnSubscribe(subscription -> log.info("Subscribed"))
                .doOnRequest(request -> log.info("request received"))
                .doOnNext(value -> log.info("doOnNext-execution. Value {}", value))
                .doOnSuccess(success -> log.info("success: ALL DONE!"))
                .doOnError(e -> log.error(e.getMessage()))
                .onErrorResume(resume -> {
                    log.info("error resume. Returning empty String ");
                    return Mono.just(noSuperHero);
                });

        StepVerifier.create(error).expectNext(noSuperHero).verifyComplete();
    }

}
