package com.example.demo1.infrastructure.adapter.in.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo1.application.service.AuthenticationService;
import com.example.demo1.infrastructure.adapter.in.web.dto.LoginRequest;
import com.example.demo1.infrastructure.adapter.in.web.dto.LoginResponse;
import com.example.demo1.infrastructure.security.TokenBlacklist;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final TokenBlacklist tokenBlacklist;

    public AuthController(
            AuthenticationService authenticationService,
            TokenBlacklist tokenBlacklist) {
        this.authenticationService = authenticationService;
        this.tokenBlacklist = tokenBlacklist;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        AuthenticationService.AuthenticationResult result =
                authenticationService.login(request.getUsername(), request.getPassword());
        return new LoginResponse(result.token(), result.role());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        tokenBlacklist.add(authorization.substring(7));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
