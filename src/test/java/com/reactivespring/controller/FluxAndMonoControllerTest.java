package com.reactivespring.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@WebFluxTest
public class FluxAndMonoControllerTest {
  @Autowired
  WebTestClient webTestClient;

  @Test
  void flux_approach1() {
    Flux<Integer> integerFlux = webTestClient.get()
        .uri("/flux")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .returnResult(Integer.class)
        .getResponseBody();

    StepVerifier.create(integerFlux)
        .expectSubscription()
        .expectNext(1)
        .expectNext(2)
        .expectNext(3)
        .expectNext(4)
        .verifyComplete();
  }

  @Test
  void flux_approach2() {
    webTestClient.get()
        .uri("/flux")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBodyList(Integer.class)
        .hasSize(4);
  }

  @Test
  void flux_approach3() {
    List<Integer> expectedIntegerList = List.of(1,2,3,4);

    EntityExchangeResult<List<Integer>> entityExchangeResult = webTestClient.get()
        .uri("/flux")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBodyList(Integer.class)
        .returnResult();

    assertThat(entityExchangeResult.getResponseBody()).isEqualTo(expectedIntegerList);
  }

  @Test
  void flux_approach4() {
    List<Integer> expectedIntegerList = List.of(1,2,3,4);

    webTestClient.get()
        .uri("/flux")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(Integer.class)
        .consumeWith(response -> assertThat(response.getResponseBody()).isEqualTo(expectedIntegerList));
  }

  @Test
  void fluxStream() {
    Flux<Long> longFlux = webTestClient.get()
        .uri("/fluxstream")
        .accept(MediaType.APPLICATION_STREAM_JSON)
        .exchange()
        .expectStatus().isOk()
        .returnResult(Long.class)
        .getResponseBody();

    StepVerifier.create(longFlux)
        .expectSubscription()
        .expectNext(0L)
        .expectNext(1L)
        .expectNext(2L)
        .thenCancel()
        .verify();
  }

  @Test
  void mono() {
    webTestClient.get()
        .uri("/mono")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Integer.class)
        .consumeWith(response -> assertThat(response.getResponseBody()).isEqualTo(1));
  }
}
