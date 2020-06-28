package com.reactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static reactor.core.scheduler.Schedulers.parallel;

public class FluxAndMonoTransformTest {

  List<String> names = List.of("adam", "anna", "jack", "jenny");

  @Test
  public void transformUsingMap() {
    Flux<String> namesFlux = Flux.fromIterable(names)
        .map(String::toUpperCase)
        .log();

    StepVerifier.create(namesFlux)
        .expectNext("ADAM", "ANNA", "JACK", "JENNY")
        .verifyComplete();
  }

  @Test
  public void transformUsingMap_length() {
    Flux<Integer> namesFlux = Flux.fromIterable(names)
        .map(name -> name.length())
        .log();

    StepVerifier.create(namesFlux)
        .expectNext(4, 4, 4, 5)
        .verifyComplete();
  }

  @Test
  public void transformUsingMap_length_repeat() {
    Flux<Integer> namesFlux = Flux.fromIterable(names)
        .map(name -> name.length())
        .repeat(1)
        .log();

    StepVerifier.create(namesFlux)
        .expectNext(4, 4, 4, 5, 4, 4, 4, 5)
        .verifyComplete();
  }

  @Test
  public void transformUsingMap_filter() {
    Flux<String> namesFlux = Flux.fromIterable(names)
        .filter(name -> name.length() > 4)
        .map(String::toUpperCase)
        .repeat(1)
        .log();

    StepVerifier.create(namesFlux)
        .expectNext("JENNY", "JENNY")
        .verifyComplete();
  }

  @Test
  public void transformUsingFlatMap() {
    Flux<String> namesFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
        .flatMap(name -> Flux.fromIterable(convertToList(name)))
        .log();

    StepVerifier.create(namesFlux)
        .expectNextCount(12)
        .verifyComplete();

  }

  private List<String> convertToList(String name) {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return Arrays.asList(name, "newValue");
  }

  @Test
  public void transformUsingFlatMap_parallel() {
    Flux<String> namesFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
        .window(2)
        .flatMap(name -> name.map(this::convertToList)
            .subscribeOn(parallel())
            .flatMap(Flux::fromIterable))
        .log();

    StepVerifier.create(namesFlux)
        .expectNextCount(12)
        .verifyComplete();

  }

  @Test
  public void transformUsingFlatMap_parallel_maintain_order() {
    Flux<String> namesFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
        .window(2)
        .flatMapSequential(name -> name.map(this::convertToList)
            .subscribeOn(parallel())
            .flatMap(Flux::fromIterable))
        .log();

    StepVerifier.create(namesFlux)
        .expectNextCount(12)
        .verifyComplete();

  }
}
