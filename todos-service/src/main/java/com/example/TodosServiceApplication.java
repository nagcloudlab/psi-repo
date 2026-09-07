package com.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

class Foo{
	public Foo() {
	}
	public void doSomething() {
	}
}

@SpringBootApplication
public class TodosServiceApplication {

	@Value("${password}")
	private String password;

	public void doFoo(){
		Foo foo = null;
		foo.doSomething();
	}

	public static void main(String[] args) {
		SpringApplication.run(TodosServiceApplication.class, args);
	}

}
