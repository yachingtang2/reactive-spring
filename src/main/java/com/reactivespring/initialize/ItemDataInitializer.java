package com.reactivespring.initialize;

import com.reactivespring.document.Item;
import com.reactivespring.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
@Profile("!test")
public class ItemDataInitializer implements CommandLineRunner {

    private List<Item> items = List.of(new Item(null, "Samsung TV", 400.0),
            new Item(null, "LG TV", 420.0),
            new Item(null, "Apple Watch", 299.99),
            new Item("My id", "Beats Headphones", 199.99));
    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    @Override
    public void run(String... args) throws Exception {
        initialDataSetUp();
    }

    private void initialDataSetUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(items))
                    .flatMap(itemReactiveRepository::save)
                    .thenMany(itemReactiveRepository.findAll())
                    .subscribe(item -> System.out.println("Item inserted from ItemDataInitializer: " + item));
    }
}
