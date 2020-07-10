package com.reactivespring.repository;

import com.reactivespring.document.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@DirtiesContext
public class ItemReactiveRepositoryTest {
  @Autowired
  private ItemReactiveRepository itemReactiveRepository;

  List<Item> items = List.of(new Item(null, "Samsung TV", 400.0),
      new Item(null, "LG TV", 420.0),
      new Item(null, "Apple Watch", 299.99),
      new Item(null, "Beats Headphones", 199.99),
      new Item("id", "Bose Headphones", 149.99)
      );

  @BeforeEach
  void setUp() {
    itemReactiveRepository.deleteAll()
      .thenMany(Flux.fromIterable(items))
      .flatMap(itemReactiveRepository::save)
      .doOnNext(item -> System.out.println("Inserted Item is: " + item))
      .blockLast();
  }

  @Test
  void getAllItems() {
    StepVerifier.create(itemReactiveRepository.findAll())
      .expectSubscription()
      .expectNextCount(5)
      .verifyComplete();
  }

  @Test
  void getItemById() {
    StepVerifier.create(itemReactiveRepository.findById("id"))
      .expectSubscription()
      .expectNextMatches(item -> item.getDescription().equals("Bose Headphones"))
      .verifyComplete();
  }

  @Test
  void findItemByDescription() {
    StepVerifier.create(itemReactiveRepository.findByDescription("LG TV")
          .log("YCT - findItemByDescription: "))
      .expectSubscription()
      .expectNextCount(1)
      .verifyComplete();
  }

  @Test
  void saveItem() {
    System.out.println("YCT - start");
    Item item = new Item(null, "Google Home Mini", 30.0);
    Mono<Item> savedItem = itemReactiveRepository.save(item);
    StepVerifier.create(savedItem.log("YCT - saveItem: "))
        .expectSubscription()
        .expectNextMatches(matchingItem -> matchingItem.getId() != null && matchingItem.getDescription().equals(
            "Google Home Mini"));
  }

  @Test
  void updateItem() {
    double newPrice = 520.00;
    Mono<Item> updatedItem = itemReactiveRepository.findByDescription("LG TV")
        .map(item -> {
          item.setPrice(newPrice);
          return item;
        })
        .flatMap(item -> itemReactiveRepository.save(item));

    StepVerifier.create(updatedItem)
        .expectSubscription()
        .expectNextMatches(item -> item.getPrice() == newPrice)
        .verifyComplete();
  }

  @Test
  void deleteItem() {
    Mono<Void> deletedItem = itemReactiveRepository.findById("id")
        .map(Item::getId)
        .flatMap(id -> itemReactiveRepository.deleteById(id));

    StepVerifier.create(deletedItem.log())
        .expectSubscription()
        .verifyComplete();

    StepVerifier.create(itemReactiveRepository.findAll().log("YCT - new item list: "))
        .expectSubscription()
        .expectNextCount(4)
        .verifyComplete();
  }

  @Test
  void deleteItemByDescription() {
    Mono<Void> deletedItem = itemReactiveRepository.findByDescription("LG TV")
        .flatMap(item -> itemReactiveRepository.delete(item));

    StepVerifier.create(deletedItem.log())
        .expectSubscription()
        .verifyComplete();

    StepVerifier.create(itemReactiveRepository.findAll().log("YCT - new item list: "))
        .expectSubscription()
        .expectNextCount(4)
        .verifyComplete();

  }
}
