package com.masterapply.backend.controller;

import com.masterapply.backend.dto.AuthResponse;
import com.masterapply.backend.dto.LoginRequest;
import com.masterapply.backend.dto.RegisterRequest;
import com.masterapply.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse inscrire(@Valid @RequestBody RegisterRequest r) {
        return service.inscrire(r);
    }

    @PostMapping("/login")
    public AuthResponse connecter(@Valid @RequestBody LoginRequest r) {
        return service.connecter(r);
    }
}