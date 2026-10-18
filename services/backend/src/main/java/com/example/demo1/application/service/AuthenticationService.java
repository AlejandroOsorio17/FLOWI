package com.example.demo1.application.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.example.demo1.infrastructure.security.AuthenticatedUser;
import com.example.demo1.infrastructure.security.JwtTokenService;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService tokenService;

    public AuthenticationService(
            AuthenticationManager authenticationManager,
            JwtTokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    public AuthenticationResult login(String username, String password) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        return new AuthenticationResult(
                tokenService.createToken(user.getUsername(), user.getRole()),
                user.getRole().name());
    }

    public record AuthenticationResult(String token, String role) {
    }
}
