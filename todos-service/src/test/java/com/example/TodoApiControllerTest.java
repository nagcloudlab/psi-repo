package com.example;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoApiController.class)
class TodoApiControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private TodoService todoService;

	@Test
	void getAllTodosReturnsListOfTodos() throws Exception {
		when(todoService.getAllTodos()).thenReturn(List.of(
				new Todo("1", "Sample Todo 1", false),
				new Todo("2", "Sample Todo 2", true)));

		mockMvc.perform(get("/api/v1/todos"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].id").value("1"))
				.andExpect(jsonPath("$[0].title").value("Sample Todo 1"))
				.andExpect(jsonPath("$[0].completed").value(false));
	}

	@Test
	void getTodoByIdReturnsOk() throws Exception {
		when(todoService.getTodoById("1")).thenReturn(Optional.of(new Todo("1", "Sample Todo 1", false)));

		mockMvc.perform(get("/api/v1/todos/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value("1"))
				.andExpect(jsonPath("$.title").value("Sample Todo 1"));
	}

	@Test
	void getTodoByIdReturns404ForMissing() throws Exception {
		when(todoService.getTodoById("999")).thenReturn(Optional.empty());

		mockMvc.perform(get("/api/v1/todos/999"))
				.andExpect(status().isNotFound());
	}

	@Test
	void getTodosReturnsJson() throws Exception {
		when(todoService.getAllTodos()).thenReturn(List.of(new Todo("1", "Todo", false)));

		mockMvc.perform(get("/api/v1/todos"))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json"));
	}

	@Test
	void createTodoReturns201() throws Exception {
		when(todoService.createTodo(any(Todo.class))).thenReturn(new Todo("abc-123", "New Todo", false));

		mockMvc.perform(post("/api/v1/todos")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"title\": \"New Todo\", \"completed\": false}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.title").value("New Todo"))
				.andExpect(jsonPath("$.id").isNotEmpty());
	}

	@Test
	void updateTodoReturnsOk() throws Exception {
		when(todoService.updateTodo(eq("1"), any(Todo.class)))
				.thenReturn(Optional.of(new Todo("1", "Updated Todo", true)));

		mockMvc.perform(put("/api/v1/todos/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"title\": \"Updated Todo\", \"completed\": true}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("Updated Todo"))
				.andExpect(jsonPath("$.completed").value(true));
	}

	@Test
	void updateTodoReturns404ForMissing() throws Exception {
		when(todoService.updateTodo(eq("999"), any(Todo.class))).thenReturn(Optional.empty());

		mockMvc.perform(put("/api/v1/todos/999")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"title\": \"Updated\", \"completed\": false}"))
				.andExpect(status().isNotFound());
	}

	@Test
	void deleteTodoReturns404ForMissing() throws Exception {
		when(todoService.deleteTodo("999")).thenReturn(false);

		mockMvc.perform(delete("/api/v1/todos/999"))
				.andExpect(status().isNotFound());
	}

	@Test
	void deleteTodoReturns204() throws Exception {
		when(todoService.deleteTodo("1")).thenReturn(true);

		mockMvc.perform(delete("/api/v1/todos/1"))
				.andExpect(status().isNoContent());
	}
}
