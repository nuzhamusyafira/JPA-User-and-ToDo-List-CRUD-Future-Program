package com.example.postgresdemo.controller;

import com.example.postgresdemo.exception.ResourceNotFoundException;
import com.example.postgresdemo.model.User;
import com.example.postgresdemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;

import javax.validation.Valid;

@RestController
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user/all")
    public Page<User> getUser(Pageable pageable){
        return userRepository.findAll(pageable);
    }

    @PostMapping("/user/create")
    public User createUser(@Valid @RequestBody User user){
        return userRepository.save(user);
    }

    @PutMapping("/user/{userId}")
    public User updateUser(@PathVariable Long userId, @Valid @RequestBody User userRequest){
        return userRepository.findById(userId).map(
                user -> {
                    user.setUsername(userRequest.getUsername());
                    return userRepository.save(user);
                }
                ).orElseThrow(
                () -> new ResourceAccessException("User " +userId+ " is not found!")
        );
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId){
        return userRepository.findById(userId).map(
                user -> {
                    userRepository.delete(user);
                    return ResponseEntity.ok().build();
                }
        ).orElseThrow(
                () -> new ResourceNotFoundException("User " +userId+ " is not found!")
        );
    }

}
