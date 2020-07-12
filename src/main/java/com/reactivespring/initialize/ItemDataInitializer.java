//package com.reactivespring.initialize;
//
//import com.reactivespring.document.Item;
//import com.reactivespring.document.ItemCapped;
//import com.reactivespring.repository.ItemReactiveCappedRepository;
//import com.reactivespring.repository.ItemReactiveRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Profile;
//import org.springframework.data.mongodb.core.CollectionOptions;
//import org.springframework.data.mongodb.core.MongoOperations;
//import org.springframework.stereotype.Component;
//import reactor.core.publisher.Flux;
//
//import java.time.Duration;
//import java.util.List;
//
//@Component
//@Profile("!test")
//@Slf4j
//public class ItemDataInitializer implements CommandLineRunner {
//
//    private final List<Item> items = List.of(new Item(null, "Samsung TV", 400.0),
//            new Item(null, "LG TV", 420.0),
//            new Item(null, "Apple Watch", 299.99),
//            new Item("My id", "Beats Headphones", 199.99));
//
//    @Autowired
//    private ItemReactiveRepository itemReactiveRepository;
//
//    @Autowired
//    private ItemReactiveCappedRepository itemReactiveCappedRepository;
//
//    @Autowired
//    private MongoOperations mongoOperations;
//
//    @Override
//    public void run(String... args) throws Exception {
//        initialDataSetUp();
//        createCappedCollection();
//        dataSetupForCappedCollection();
//    }
//
//    private void createCappedCollection() {
//        mongoOperations.dropCollection(ItemCapped.class);
//        mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty()
//            .maxDocuments(20)
//            .size(50000)
//            .capped());
//    }
//
//    private void initialDataSetUp() {
//        itemReactiveRepository.deleteAll()
//                .thenMany(Flux.fromIterable(items))
//                    .flatMap(itemReactiveRepository::save)
//                    .thenMany(itemReactiveRepository.findAll())
//                    .subscribe(item -> System.out.println("Item inserted from ItemDataInitializer: " + item));
//    }
//
//    public void dataSetupForCappedCollection() {
//        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(1))
//            .map(value -> new ItemCapped(null, "Random Item " + value, (100.00 + value)));
//
//        itemReactiveCappedRepository.insert(itemCappedFlux)
//            .subscribe(itemCapped -> log.info("Inserted capped item is: " + itemCapped));
//    }
//
//    public void dataSetUpforCappedCollection(){
//
//        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(1))
//            .map(i -> new ItemCapped(null,"Random Item " + i, (100.00+i)));
//
//        itemReactiveCappedRepository
//            .insert(itemCappedFlux)
//            .subscribe((itemCapped -> {
//                log.info("Inserted Item is " + itemCapped);
//            }));
//
//    }
//}

package com.reactivespring.initialize;

import com.reactivespring.document.Item;
import com.reactivespring.document.ItemCapped;
import com.reactivespring.repository.ItemReactiveCappedRepository;
import com.reactivespring.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Component
@Profile("!test")
@Slf4j
public class ItemDataInitializer implements CommandLineRunner {

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    @Autowired
    private ItemReactiveCappedRepository itemReactiveCappedRepository;

    @Autowired
    private MongoOperations mongoOperations;

    @Override
    public void run(String... args) throws Exception {

        initialSetUp();
        createCappedCollection();
        dataSetUpForCappedCollection();
    }

    private void createCappedCollection() {
        mongoOperations.dropCollection(ItemCapped.class);
        mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty().maxDocuments(20).size(50000).capped());
    }

    public List<Item> data() {

        return Arrays.asList(new Item(null, "Samsung TV", 399.99),
            new Item(null, "LG TV", 329.99),
            new Item(null, "Apple Watch", 349.99),
            new Item("ABC", "Beats HeadPhones", 149.99));
    }

    public void dataSetUpForCappedCollection(){

        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(1))
            .map(i -> new ItemCapped(null,"Random Item " + i, (100.00+i)));

        itemReactiveCappedRepository
            .insert(itemCappedFlux)
            .subscribe((itemCapped -> {
                log.info("Inserted Item is " + itemCapped);
            }));
    }

    private void initialSetUp() {

        itemReactiveRepository.deleteAll()
            .thenMany(Flux.fromIterable(data()))
            .flatMap(itemReactiveRepository::save)
            .thenMany(itemReactiveRepository.findAll())
            .subscribe((item -> {
                System.out.println("Item inserted from CommandLineRunner : " + item);
            }));

    }



}

