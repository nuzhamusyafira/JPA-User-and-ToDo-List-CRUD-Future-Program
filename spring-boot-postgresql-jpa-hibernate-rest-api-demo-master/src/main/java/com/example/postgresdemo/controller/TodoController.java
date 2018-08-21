package com.example.postgresdemo.controller;

import com.example.postgresdemo.exception.ResourceNotFoundException;
import com.example.postgresdemo.model.Todo;
import com.example.postgresdemo.repository.TodoRepository;
import com.example.postgresdemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class TodoController {
    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user/{userId}/todo")
    public List<Todo> getUserTodo(@PathVariable Long userId){
        return todoRepository.findAllByUserId(userId);
    }

    @PostMapping("/user/{userId}/todo")
    public Todo createTodo(@PathVariable Long userId, @Valid @RequestBody Todo todo){
        return userRepository.findById(userId).map(
                user -> {
                    todo.setUser(user);
                    return todoRepository.save(todo);
                }
        ).orElseThrow(
                () -> new ResourceNotFoundException("User " +userId+ " is not found!")
        );
    }

    @PutMapping("/user/{userId}/todo/{todoId}")
    public Todo updateTodo(@PathVariable Long userId, @PathVariable Long todoId, @Valid @RequestBody Todo todoRequest){
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User " +userId+ " is not found!");
        }

        return todoRepository.findById(todoId).map(
                todo -> {
                    todo.setDescription(todoRequest.getDescription());
                    return todoRepository.save(todo);
                }
        ).orElseThrow(
                () -> new ResourceNotFoundException("Todo " +todoId+ " is not found!")
        );
    }

    @DeleteMapping("/user/{userId}/todo/{todoId}")
    public ResponseEntity<?> deleteTodo(@PathVariable Long userId, @PathVariable Long todoId){
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User " +userId+ " is not found!");
        }

        return todoRepository.findById(todoId).map(
                todo -> {
                    todoRepository.delete(todo);
                    return ResponseEntity.ok().build();
                }
        ).orElseThrow(
                () -> new ResourceNotFoundException("Todo " +todoId+ " is not found!")
        );
    }
}
