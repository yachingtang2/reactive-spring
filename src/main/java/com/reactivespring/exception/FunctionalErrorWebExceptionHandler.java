package com.reactivespring.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.error.ErrorAttributeOptions.Include;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class FunctionalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

  public FunctionalErrorWebExceptionHandler(ErrorAttributes errorAttributes, ApplicationContext applicationContext,
                                            ServerCodecConfigurer serverCodecConfigurer) {
    super(errorAttributes, new ResourceProperties(), applicationContext);
    super.setMessageWriters(serverCodecConfigurer.getWriters());
    super.setMessageReaders(serverCodecConfigurer.getReaders());
  }

  @Override
  protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
    return RouterFunctions
        .route(RequestPredicates.all(), this::renderErrorResponse);
  }

  private Mono<ServerResponse> renderErrorResponse(ServerRequest serverRequest) {

    Map<String, Object> errorAttributesMap = getErrorAttributes(serverRequest, ErrorAttributeOptions.of(Include.MESSAGE));
    log.info("errorAttributesMap: <" + errorAttributesMap + ">");
    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .contentType(MediaType.APPLICATION_JSON)
              .body(BodyInserters.fromValue(errorAttributesMap.get("message")));
  }
}
