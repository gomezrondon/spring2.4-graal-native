package com.gomezrondon.app;



import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.r2dbc.core.DatabaseClient;

import java.util.List;

//@Configuration
public class ConfigApplication {


/*
    @Bean
    DatabaseClient getDatabseClient() {
       ConnectionFactory connectionFactory = ConnectionFactories.get("r2dbc:mysql://localhost:3306/movie");

        return DatabaseClient.create(connectionFactory);
    }*/

    @Bean
    ApplicationRunner applicationRunner(  MovieService service) {
        return args -> {

            ConnectionFactory connectionFactory = ConnectionFactories.get("r2dbc:pool:mysql://localhost:3306/movie");
            DatabaseClient dbc = DatabaseClient.create(connectionFactory);
            Flux<Movie> firstMovie = insertMovieFlux(service, List.of("Matrix"));

            List<String> movieList = List.of( "Terminator", "RoboCop", "Alien II", "RoboCop2", "Batman Begins ", "Matrix 2", "Transformers", "Limitless");
            Flux<Movie> movieFlux = insertMovieFlux(service, movieList);

            Mono<Integer> dropTable = dbc.sql("DROP TABLE IF EXISTS movie;")
                    .fetch()
                    .rowsUpdated();

            Mono<Integer> createTable = dbc.sql("create table movie (  id   int auto_increment  PRIMARY KEY,  name varchar(50) not null );")
                    .fetch()
                    .rowsUpdated();

            dropTable.log(">>>> drop table >>> ")
                    .then(createTable)
                    .thenMany(service.deleteAll())    // delete all records
                    .thenMany(firstMovie)             // guaranty to be the first
                    .thenMany(movieFlux)             // insert all records
                    .doOnNext(System.out::println)
                    .blockLast(); // to allow records to be inserted before running test

        };
    }


    private Flux<Movie> insertMovieFlux(MovieService service, List<String> movieList) {
        return Flux.fromIterable(movieList)
                .map(String::toLowerCase)
                .map(Movie::new)
                .flatMap(movie -> {
                    if (movie.getName().equals("matrix")) {
                        return service.insert(movie.withId(1));
                    } else {
                        return service.save(movie);
                    }
                });
    }

}
