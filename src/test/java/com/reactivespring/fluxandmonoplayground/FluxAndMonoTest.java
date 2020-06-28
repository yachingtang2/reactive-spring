package com.reactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoTest {

  @Test
  public void fluxTest() {
    Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
//        .concatWith(Flux.error(new RuntimeException("Error occurred")))
        .concatWith(Flux.just("After error"))
        .log();

    stringFlux.subscribe(System.out::println,
        System.err::println,
        () -> System.out.println("Completed"));
  }

  @Test
  public void fluxTestElements_withErrors() {
    Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
        .concatWith(Flux.error(new RuntimeException("error occurred")))
        .log();

    StepVerifier.create(stringFlux)
        .expectNext("Spring", "Spring Boot", "Reactive Spring")
//        .expectError(RuntimeException.class)
        .expectErrorMessage("error occurred")
        .verify();

  }

  @Test
  public void fluxTestElementsCount_withErrors() {
    Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
        .concatWith(Flux.error(new RuntimeException("error occurred")))
        .log();

    StepVerifier.create(stringFlux)
        .expectNextCount(3)
        .expectErrorMessage("error occurred")
        .verify();

  }

  @Test
  public void monoTest() {
    Mono<String> stringMono = Mono.just("Spring");

    StepVerifier.create(stringMono.log())
        .expectNext("Spring")
        .verifyComplete();
  }

  @Test
  public void monoTest_error() {
    Mono<String> stringMono = Mono.just("Spring");

    StepVerifier.create(Mono.error(new RuntimeException("error...")).log())
        .expectError(RuntimeException.class)
        .verify();
  }
}
