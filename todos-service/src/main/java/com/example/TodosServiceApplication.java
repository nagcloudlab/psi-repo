package com.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class TodosServiceApplication {

	@Value("${password}")
	private String password;

	public void doSomething() {
		String s=null;
		String ss=s.toUpperCase();
	}

	public static void main(String[] args) {
		SpringApplication.run(TodosServiceApplication.class, args);
	}

}
