package com.reactivespring.router;

import com.reactivespring.handler.ItemHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.reactivespring.constants.ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1;
import static com.reactivespring.constants.ItemConstants.ITEM_STREAM_FUNCTIONAL_END_POINT_V1;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ItemRouter {

    @Bean
    public RouterFunction<ServerResponse> itemRoute(ItemHandler itemHandler) {
        return RouterFunctions
                .route(GET(ITEM_FUNCTIONAL_END_POINT_V1).and(accept(MediaType.APPLICATION_JSON)), itemHandler::getAllItems)
                .andRoute(GET(ITEM_FUNCTIONAL_END_POINT_V1 + "/{id}").and(accept(MediaType.APPLICATION_JSON)), itemHandler::getOneItem)
                .andRoute(POST(ITEM_FUNCTIONAL_END_POINT_V1).and(accept(MediaType.APPLICATION_JSON)), itemHandler::createItem)
                .andRoute(DELETE(ITEM_FUNCTIONAL_END_POINT_V1 + "/{id}").and(accept(MediaType.APPLICATION_JSON)), itemHandler::deleteItem)
                .andRoute(PUT(ITEM_FUNCTIONAL_END_POINT_V1 + "/{id}").and(accept(MediaType.APPLICATION_JSON)), itemHandler::updateItem);
    }

    @Bean
    public RouterFunction<ServerResponse> errorRoute(ItemHandler itemHandler) {
        return RouterFunctions
            .route(GET("/fun/runtimeException").and(accept(MediaType.APPLICATION_JSON)), itemHandler::itemException);
    }

    @Bean
    public RouterFunction<ServerResponse> itemStreamRouter(ItemHandler itemHandler) {
        return RouterFunctions
            .route(GET(ITEM_STREAM_FUNCTIONAL_END_POINT_V1).and(accept(APPLICATION_JSON)),
                itemHandler::itemStream);
    }
}
