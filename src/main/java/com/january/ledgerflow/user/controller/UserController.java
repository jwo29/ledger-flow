package com.january.ledgerflow.user.controller;

import com.january.ledgerflow.user.dto.UserCreateRequestDTO;
import com.january.ledgerflow.user.dto.UserResponseDTO;
import com.january.ledgerflow.user.repository.UserRepository;
import com.january.ledgerflow.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public Long createUser(@RequestBody UserCreateRequestDTO userCreateRequestDTO) {
        return userService.createUser(userCreateRequestDTO);
    }

    @GetMapping("/{id}")
    public UserResponseDTO getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

}
