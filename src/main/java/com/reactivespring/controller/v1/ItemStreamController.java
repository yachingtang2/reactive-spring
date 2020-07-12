package com.reactivespring.controller.v1;

import com.reactivespring.document.ItemCapped;
import com.reactivespring.repository.ItemReactiveCappedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static com.reactivespring.constants.ItemConstants.ITEM_STREAM_END_POINT_V1;

//@RestController
public class ItemStreamController {

  @Autowired
  private ItemReactiveCappedRepository itemReactiveCappedRepository;

  @GetMapping(value = ITEM_STREAM_END_POINT_V1, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
  public Flux<ItemCapped> getItemsStream() {
    return itemReactiveCappedRepository.findItemsBy();
  }
}

