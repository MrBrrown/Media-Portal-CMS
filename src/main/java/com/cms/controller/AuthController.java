package com.cms.controller;

import com.cms.dto.AuthResponseDto;
import com.cms.dto.UserDto;
import com.cms.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody UserDto.UserRegistrationDto registrationDto) {
        AuthResponseDto response = userService.register(registrationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody UserDto.UserLoginDto loginDto) {
        AuthResponseDto response = userService.login(loginDto);
        return ResponseEntity.ok(response);
    }
}

