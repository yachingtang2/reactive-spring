package com.reactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.function.Supplier;

public class FluxAndMonoFactoryTest {

  List<String> names = List.of("adam", "anna", "jack", "jenny");

  @Test
  public void fluxUsingIterable() {

    Flux<String> namesFlux = Flux.fromIterable(names)
        .log();

    StepVerifier.create(namesFlux)
        .expectNext("adam", "anna", "jack", "jenny")
        .verifyComplete();
  }

  @Test
  public void fluxUsingArray() {
    String[] names = new String[] {"adam", "anna", "jack", "jenny"};
    Flux<String> namesFlux = Flux.fromArray(names)
        .log();

    StepVerifier.create(namesFlux)
        .expectNext("adam", "anna", "jack", "jenny")
        .verifyComplete();
  }

  @Test
  public void fluxUsingStream() {
    Flux<String> namesFlux = Flux.fromStream(names.stream())
        .log();

    StepVerifier.create(namesFlux)
        .expectNext("adam", "anna", "jack", "jenny")
        .verifyComplete();
  }

  @Test
  public void monoUsingJustOrEmpty() {
    Mono<String> mono = Mono.justOrEmpty(null);

    StepVerifier.create(mono.log())
        .verifyComplete();
  }

  @Test
  public void monoUsingSupplier() {
    Supplier<String> stringSupplier = () -> "adam";
    Mono<String> stringMono = Mono.fromSupplier(stringSupplier);

    StepVerifier.create(stringMono.log())
        .expectNext(stringSupplier.get())
        .verifyComplete();
  }

  @Test
  public void fluxUsingRange() {
    Flux<Integer> integerFlux = Flux.range(1, 5)
        .log();

    StepVerifier.create(integerFlux)
        .expectNext(1, 2, 3, 4, 5)
        .verifyComplete();
  }
}
