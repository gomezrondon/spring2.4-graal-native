package com.gomezrondon.app;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovieService {

    Flux<Movie> findAll();

    Mono<Void> delete(int id);

    Mono<Movie> insert(Movie movie);

    Mono<Movie> save(Movie movie);

    Mono<Void> deleteAll();
}
