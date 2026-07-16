package com.pge.account_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pge.account_service.domain.User;
import com.pge.account_service.repository.UserRepository;

@RestController
@RequestMapping("/accounts")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        return userRepository.findById(id)
            .map(user -> ResponseEntity.ok().body(user))
            .orElse((ResponseEntity.notFound().build()));
    }

    @GetMapping
    public ResponseEntity<List<User>> findAll() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok().body(users);
    }
}
