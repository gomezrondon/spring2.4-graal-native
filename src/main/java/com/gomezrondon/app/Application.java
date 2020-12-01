package com.gomezrondon.app;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication
@RestController
public class Application  {

	private final MovieRepository repository;

	public Application(MovieRepository repository) {
		this.repository = repository;
	}

	@GetMapping("/movies")
	public Flux<Movie> getAllMovies() {
		return repository.findAll();
	}

	@Bean
	ApplicationRunner runner(DatabaseClient dbc, MovieRepository repository) {
		return args -> {

			dbc.sql("create table movie (  id   int auto_increment  PRIMARY KEY,  name varchar(50) not null );")
					.fetch()
					.rowsUpdated()
					.then(repository.save(new Movie(null, "Matrix")))
					.subscribe();
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
