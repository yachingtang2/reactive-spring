package com.reactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class ColdAndHotPublisherTest {
  @Test
  void coldPublisherTest() throws InterruptedException {
    Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
        .delayElements(Duration.ofSeconds(1))
        .log();

    stringFlux.subscribe(value -> System.out.println("Subscribe 1: " + value));

    Thread.sleep(2000);

    stringFlux.subscribe(value -> System.out.println("Subscribe 2: " + value));

    Thread.sleep(4000);
  }

  @Test
  void hotPublisherTest() throws InterruptedException {
    Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
        .delayElements(Duration.ofSeconds(1))
        .log();

    ConnectableFlux<String> connectableFlux = stringFlux.publish();
    connectableFlux.connect();
    connectableFlux.subscribe(value -> System.out.println("Subscribe 1: " + value));
    Thread.sleep(3000);

    connectableFlux.subscribe(value -> System.out.println("Subscribe 2: " + value));
    Thread.sleep(4000);
  }
}
