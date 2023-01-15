package com.sk.fresh.service.crm.webclient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
public class CRMWebClientHandler {

     private final WebClient webClient;

     /**
      * Fetch a list of object using generic types.
      *
      * @param parameterizedTypeReference Definition of the object type to retrieve
      * @param uriBuilderFunction         The builder function for the endpoint. The base endpoint is defined in the {@link WebClientConfig}.
      * @param <T>                        The type definition of the Object
      * @return Returns the object type requested.
      */
     public <T> Mono<List<T>> fetchList(
             final ParameterizedTypeReference<List<T>> parameterizedTypeReference,
             final Function<UriBuilder, URI> uriBuilderFunction) {

          return webClient
                  .get()
                  .uri(uriBuilder -> uriBuilderFunction.apply(uriBuilder))
                  .retrieve()
                  .onStatus(
                          HttpStatus::isError,
                          clientResponse ->
                                  clientResponse
                                          .bodyToMono(WebClientResponseException.class)
                                          .flatMap(bodyValue -> Mono.error(bodyValue)))
                  .bodyToMono(parameterizedTypeReference);
     }

     /**
      * Fetch a single object
      *
      * @param uriBuilderFunction  The builder function for the endpoint. The base endpoint is defined in the {@link WebClientConfig}.
      * @param modelImplementation Definition of the object type to fetch.
      * @param <T>
      * @return The single object Mono.
      */
     public <T> Mono<T> fetchSingleObject(
             final Function<UriBuilder, URI> uriBuilderFunction,
             final Class<T> modelImplementation) {
          return webClient
                  .get()
                  .uri(uriBuilder -> uriBuilderFunction.apply(uriBuilder))
                  .retrieve()
                  .onStatus(
                          HttpStatus::isError,
                          clientResponse ->
                                  clientResponse
                                          .bodyToMono(WebClientResponseException.class)
                                          .flatMap(bodyValue -> Mono.error(bodyValue)))
                  .bodyToMono(modelImplementation);
     }

     /**
      * @param uriBuilderFunction  The builder function for the endpoint. The base endpoint is defined in the {@link WebClientConfig}.
      * @param request
      * @param modelImplementation
      * @param <T>
      * @return
      */
     public <T> Mono<T> update(
             final Function<UriBuilder, URI> uriBuilderFunction,
             final T request,
             final Class<T> modelImplementation) {
          return webClient
                  .put()
                  .uri(uriBuilder -> uriBuilderFunction.apply(uriBuilder))
                  .body(Mono.just(request), modelImplementation)
                  .retrieve()
                  .onStatus(
                          HttpStatus::isError,
                          clientResponse ->
                                  clientResponse
                                          .bodyToMono(WebClientResponseException.class)
                                          .flatMap(bodyValue -> Mono.error(bodyValue)))
                  .bodyToMono(modelImplementation);
     }

     /**
      * @param uriBuilderFunction The builder function for the endpoint. The base endpoint is defined in the {@link WebClientConfig}.
      * @param request
      * @param responseModelClass
      * @param <T>
      * @param <U>
      * @return
      */
     public <T, U> Mono<T> add(
             final Function<UriBuilder, URI> uriBuilderFunction,
             final U request,
             final Class<T> responseModelClass) {
          return webClient
                  .post()
                  .uri(uriBuilder -> uriBuilderFunction.apply(uriBuilder))
                  .body(BodyInserters.fromObject(request))
                  .retrieve()
                  .onStatus(
                          HttpStatus::isError,
                          clientResponse ->
                                  clientResponse
                                          .bodyToMono(WebClientResponseException.class)
                                          .flatMap(bodyValue -> Mono.error(bodyValue)))
                  .bodyToMono(responseModelClass);
     }

     /**
      * @param uriBuilderFunction         The builder function for the endpoint. The base endpoint is defined in the {@link WebClientConfig}.
      * @param request
      * @param parameterizedTypeReference
      * @param <T>
      * @param <U>
      * @return
      */
     public <T, U> Mono<List<T>> create(
             final Function<UriBuilder, URI> uriBuilderFunction,
             final U request,
             final ParameterizedTypeReference<List<T>> parameterizedTypeReference) {
          return webClient
                  .post()
                  .uri(uriBuilder -> uriBuilderFunction.apply(uriBuilder))
                  .body(BodyInserters.fromObject(request))
                  .retrieve()
                  .onStatus(
                          HttpStatus::isError,
                          clientResponse ->
                                  clientResponse
                                          .bodyToMono(WebClientResponseException.class)
                                          .flatMap(bodyValue -> Mono.error(bodyValue)))
                  .bodyToMono(parameterizedTypeReference);
     }

     /**
      * @param uriBuilderFunction  The builder function for the endpoint. The base endpoint is defined in the {@link WebClientConfig}.
      * @param modelImplementation
      * @param <T>
      * @return
      */
     public <T> Mono<T> delete(
             final Function<UriBuilder, URI> uriBuilderFunction,
             final Class<T> modelImplementation) {
          return webClient
                  .delete()
                  .uri(uriBuilder -> uriBuilderFunction.apply(uriBuilder))
                  .retrieve()
                  .onStatus(
                          HttpStatus::isError,
                          clientResponse ->
                                  clientResponse
                                          .bodyToMono(WebClientResponseException.class)
                                          .flatMap(bodyValue -> Mono.error(bodyValue)))
                  .bodyToMono(modelImplementation);
     }
}
