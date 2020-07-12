package com.reactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;

import java.time.Duration;

public class FluxAndMonoErrorTest {
  @Test
  void fluxErrorHandling() {
    Flux<String> stringFlux = Flux.just("A", "B", "C")
        .concatWith(Flux.error(new RuntimeException("error occurred")))
        .concatWith(Flux.just("D"))
        .onErrorResume(exception -> {
          System.out.println("Exception is" + exception);
          return Flux.just("default", "default1");
        });

    StepVerifier.create(stringFlux.log())
        .expectSubscription()
        .expectNext("A", "B", "C")
//        .expectError(RuntimeException.class)
//        .verify();
        .expectNext("default", "default1")
        .verifyComplete();
  }

  @Test
  void fluxErrorHandling_onErrorReturn() {
    Flux<String> stringFlux = Flux.just("A", "B", "C")
        .concatWith(Flux.error(new RuntimeException("error occurred")))
        .concatWith(Flux.just("D"))
        .onErrorReturn("default");

    StepVerifier.create(stringFlux.log())
        .expectSubscription()
        .expectNext("A", "B", "C")
        .expectNext("default")
        .verifyComplete();
  }

  @Test
  void fluxErrorHandling_onErrorMap() {
    Flux<String> stringFlux = Flux.just("A", "B", "C")
        .concatWith(Flux.error(new RuntimeException("error occurred")))
        .concatWith(Flux.just("D"))
        .onErrorMap(CustomException::new);

    StepVerifier.create(stringFlux.log())
        .expectSubscription()
        .expectNext("A", "B", "C")
        .expectError(CustomException.class)
        .verify();
  }

  @Test
  void fluxErrorHandling_onErrorMap_withRetry() {
    Flux<String> stringFlux = Flux.just("A", "B", "C")
        .concatWith(Flux.error(new RuntimeException("error occurred")))
        .concatWith(Flux.just("D"))
        .onErrorMap(CustomException::new)
        .retry(2);

    StepVerifier.create(stringFlux.log())
        .expectSubscription()
        .expectNext("A", "B", "C")
        .expectNext("A", "B", "C")
        .expectNext("A", "B", "C")
        .expectError(CustomException.class)
        .verify();
  }

  @Test
  void fluxErrorHandling_onErrorMap_withRetryBackOff() {
    Flux<String> stringFlux = Flux.just("A", "B", "C")
        .concatWith(Flux.error(new RuntimeException("error occurred")))
        .concatWith(Flux.just("D"))
        .onErrorMap(CustomException::new)
        .retryWhen(Retry.backoff(2, Duration.ofSeconds(5)));

    StepVerifier.create(stringFlux.log())
        .expectSubscription()
        .expectNext("A", "B", "C")
        .expectNext("A", "B", "C")
        .expectNext("A", "B", "C")
        .expectError(IllegalStateException.class)
        .verify();
  }
}
