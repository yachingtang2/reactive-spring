package com.reactivespring.handler;

import com.reactivespring.document.Item;
import com.reactivespring.repository.ItemReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static com.reactivespring.constants.ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemHandlerTest {

    private final List<Item> items = List.of(new Item(null, "Samsung TV", 400.0),
            new Item(null, "LG TV", 420.0),
            new Item(null, "Apple Watch", 299.99),
            new Item("My id", "Beats Headphones", 199.99));

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    @BeforeEach
    void setUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(items))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> System.out.println("Item inserted from ItemControllerTest is: " + item))
                .blockLast();
    }

    @Test
    void getAllItems() {
        webTestClient.get().uri(ITEM_FUNCTIONAL_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(4);
    }

    @Test
    void getAllItems_approach2() {
        webTestClient.get().uri(ITEM_FUNCTIONAL_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(4)
                .consumeWith(response -> {
                    List<Item> items = response.getResponseBody();
                    assert items != null;
                    items.forEach(item -> assertNotNull(item.getId()));
                });
    }

    @Test
    void getAllItems_approach3() {
        Flux<Item> itemFlux = webTestClient.get().uri(ITEM_FUNCTIONAL_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(Item.class)
                .getResponseBody();

        StepVerifier.create(itemFlux.log("value from network: "))
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    void getOneItem() {
        webTestClient.get().uri(ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "My id")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price", 199.99);
    }

    @Test
    void getOneItem_notFound() {
        webTestClient.get().uri(ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "id")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void createItem() {
        Item item = new Item(null, "Acer laptop", 599.99);
        webTestClient.post().uri(ITEM_FUNCTIONAL_END_POINT_V1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo("Acer laptop")
                .jsonPath("$.price").isEqualTo(599.99);
    }

    @Test
    void deleteItem() {
        webTestClient.delete().uri(ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "My id")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }

    @Test
    void updateItem() {
        double newPrice = 139.99;
        Item item = new Item(null, "Beats Headphones", newPrice);

        webTestClient.put().uri(ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "My id")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price", newPrice);
    }

    @Test
    void updateItem_notFound() {
        double newPrice = 139.99;
        Item item = new Item(null, "Beats Headphones", newPrice);

        webTestClient.put().uri(ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "id")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isNotFound();
    }
}
