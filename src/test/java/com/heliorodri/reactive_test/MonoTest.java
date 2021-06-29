package com.heliorodri.reactive_test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
public class MonoTest {

    @Test
    public void monoSubscriberTest() {
        String superHero = "Iron man";
        Mono<String> publisher = Mono.just(superHero).log();

        StepVerifier.create(publisher).expectNext(superHero).verifyComplete();
    }

}
