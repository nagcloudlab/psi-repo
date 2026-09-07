package com.example;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController 
@RequestMapping("/api/v1/todos")
public class TodoApiController {

    @GetMapping
    public List<Todo> getTodos() {
        // This is just a placeholder. You would typically fetch this from a database.
        return List.of(
            new Todo("1", "Sample Todo 1", false),
            new Todo("2", "Sample Todo 2", true)
        );
    }
    
}
