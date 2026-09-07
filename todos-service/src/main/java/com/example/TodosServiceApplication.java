package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TodosServiceApplication {

	private static final String APPLICATION_NAME = "Todos Service";
	private String password;;

	public static void main(String[] args) {
		SpringApplication.run(TodosServiceApplication.class, args);
	}

}
