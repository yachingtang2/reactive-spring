package com.reactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

public class FluxAndMonoFilterTest {

  List<String> names = List.of("adam", "anna", "jack", "jenny");

  @Test
  public void filterTest() {
    Flux<String> namesFlux = Flux.fromIterable(names)
        .filter(name -> name.startsWith("a"))
        .log();

    StepVerifier.create(namesFlux)
        .expectNext("adam", "anna")
        .verifyComplete();
  }

  @Test
  public void filterTestLength() {
    Flux<String> namesFlux = Flux.fromIterable(names)
        .filter(name -> name.length() > 4)
        .log();

    StepVerifier.create(namesFlux)
        .expectNext("jenny")
        .verifyComplete();
  }
}
