package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoApiController.class)
class TodoApiControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void getTodosReturnsListOfTodos() throws Exception {
		mockMvc.perform(get("/api/v1/todos"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].id").value("1"))
				.andExpect(jsonPath("$[0].title").value("Sample Todo 1"))
				.andExpect(jsonPath("$[0].completed").value(false))
				.andExpect(jsonPath("$[1].id").value("2"))
				.andExpect(jsonPath("$[1].title").value("Sample Todo 2"))
				.andExpect(jsonPath("$[1].completed").value(true));
	}

	@Test
	void getTodosReturnsJson() throws Exception {
		mockMvc.perform(get("/api/v1/todos"))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json"));
	}
}
