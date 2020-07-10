package com.reactivespring.repository;

import com.reactivespring.document.Item;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ItemReactiveRepository extends ReactiveMongoRepository<Item, String> {

  Mono<Item> findByDescription(String description);
}
