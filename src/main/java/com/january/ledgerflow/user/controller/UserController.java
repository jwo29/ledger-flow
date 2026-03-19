package com.january.ledgerflow.user.controller;

import com.january.ledgerflow.common.response.ApiResponse;
import com.january.ledgerflow.user.dto.UserCreateRequestDTO;
import com.january.ledgerflow.user.dto.UserResponseDTO;
import com.january.ledgerflow.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ApiResponse<Long> createUser(@RequestBody UserCreateRequestDTO userCreateRequestDTO) {
        Long userId = userService.createUser(userCreateRequestDTO);
        return ApiResponse.success(userId);
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponseDTO> getUser(@PathVariable Long id) {
        return ApiResponse.success(userService.getUser(id));
    }

}
