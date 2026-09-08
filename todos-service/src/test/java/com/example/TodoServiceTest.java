package com.example;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TodoServiceTest {

	@Autowired
	private TodoRepository todoRepository;

	private TodoService todoService;

	@BeforeEach
	void setUp() {
		todoRepository.deleteAll();
		todoRepository.save(new Todo(null, "Sample Todo 1", false));
		todoRepository.save(new Todo(null, "Sample Todo 2", true));
		todoRepository.save(new Todo(null, "Sample Todo 3", false));
		todoService = new TodoService(todoRepository);
	}

	@Test
	void getAllTodosReturnsInitialTodos() {
		List<Todo> todos = todoService.getAllTodos();
		assertEquals(3, todos.size());
	}

	@Test
	void getTodoByIdReturnsExistingTodo() {
		Todo saved = todoRepository.findAll().get(0);
		Optional<Todo> todo = todoService.getTodoById(saved.getId());
		assertTrue(todo.isPresent());
		assertEquals(saved.getTitle(), todo.get().getTitle());
	}

	@Test
	void getTodoByIdReturnsEmptyForMissing() {
		Optional<Todo> todo = todoService.getTodoById("nonexistent");
		assertTrue(todo.isEmpty());
	}

	@Test
	void createTodoAssignsIdAndAdds() {
		Todo newTodo = new Todo(null, "New Todo", false);
		Todo created = todoService.createTodo(newTodo);

		assertNotNull(created.getId());
		assertEquals("New Todo", created.getTitle());
		assertEquals(4, todoService.getAllTodos().size());
	}

	@Test
	void updateTodoModifiesExisting() {
		Todo saved = todoRepository.findAll().get(0);
		Todo updated = new Todo(null, "Updated Title", true);
		Optional<Todo> result = todoService.updateTodo(saved.getId(), updated);

		assertTrue(result.isPresent());
		assertEquals("Updated Title", result.get().getTitle());
		assertTrue(result.get().isCompleted());
	}

	@Test
	void updateTodoReturnsEmptyForMissing() {
		Todo updated = new Todo(null, "Updated", false);
		Optional<Todo> result = todoService.updateTodo("nonexistent", updated);
		assertTrue(result.isEmpty());
	}

	@Test
	void deleteTodoRemovesExisting() {
		Todo saved = todoRepository.findAll().get(0);
		assertTrue(todoService.deleteTodo(saved.getId()));
		assertEquals(2, todoService.getAllTodos().size());
	}

	@Test
	void deleteTodoReturnsFalseForMissing() {
		assertFalse(todoService.deleteTodo("nonexistent"));
	}
}
