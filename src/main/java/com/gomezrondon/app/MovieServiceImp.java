package com.gomezrondon.app;


import io.netty.util.internal.StringUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class MovieServiceImp implements MovieService {


        private final MovieRepository repository;

        public MovieServiceImp(MovieRepository repository) {
            this.repository = repository;
        }

        @Override
        public Flux<Movie> findAll() {
            return repository.findAll();
        }

        public Mono<Movie> findById(int id) {

/*        return  repository.findById(id).hasElement()
                .flatMap(isEmpty -> {
                    log.info(">>>>>>>>>>>>>>>>>>>> "+isEmpty);
                    if (isEmpty) {
                        return repository.findById(id);
                    } else {
                        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));
                    }
                });*/

            return repository.findById(id).log(">>>findById: ");
            //   .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found")));


        }

        @Override
        public Mono<Movie> save(Movie movie) {
            return repository.save(movie);
        }

        @Override
        public Mono<Movie> insert(Movie movie) {
            return repository.insertMovie(movie.getId(), movie.getName());
        }


        public Mono<Void> update(Movie movie) {
            return findById(movie.getId())
                    //.log("update movie: "+movie.toString())
                    .map(movieFound -> movie) // setId returns void | withId return itself
                    .flatMap(repository::save)
                    .then();
            //.thenEmpty(Mono.empty());
        }

        @Override
        public Mono<Void> delete(int id) {
            return findById(id).flatMap(repository::delete)
                    .then();
        }

        @Override
        public Mono<Void> deleteAll() {
            return repository.deleteAll();
        }


        public Mono<Movie> findByTitle(String title) {
            return repository.findByName(title);
        }


        @Transactional
        public Flux<Movie> saveAll(List<Movie> movies) {
            // con validation a priori
            return Flux.fromIterable(movies)
                    .flatMap(this::validateMovie)
                    .flatMap(repository::save);
        }



        @Transactional
        public Flux<Movie> saveAll(Flux<Movie> movies) {

            return repository.saveAll(movies.flatMap(this::validateMovie));


            // con validation a priori
            // return
        }

        private Flux<Movie> validateMovie(Movie movie) {
            if (StringUtil.isNullOrEmpty(movie.getName())) {
                return Flux.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Name"));
            } else {
                return Flux.just(movie);
            }
        }


}
