package com.gomezrondon.app;


import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;


public interface MovieRepository extends ReactiveCrudRepository<Movie, Integer> {
    Mono<Movie> findById(int id);

    Mono<Movie> findByName(String title);

    @Query("Insert into movie(id, name) VALUES (?, ?)")
    Mono<Movie> insertMovie(int id, String title);
}
