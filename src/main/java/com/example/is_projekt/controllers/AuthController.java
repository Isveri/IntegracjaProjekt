package com.example.is_projekt.controllers;

import com.example.is_projekt.model.User;
import com.example.is_projekt.model.auth.TokenResponse;
import com.example.is_projekt.services.AuthService;
import com.example.project.model.auth.UserCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Objects;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = AuthController.BASE_AUTH_URL)
public class AuthController {

    private final AuthService authService;

    public static final String BASE_AUTH_URL = "/api/v1/auth";

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody UserCredentials userCredentials) {
        TokenResponse token = authService.getToken(userCredentials);
        if (Objects.nonNull(token)) {
            return ResponseEntity.ok(token);
        } else {
            return new ResponseEntity<>("Login error", HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/new-account")
    public ResponseEntity<TokenResponse> createNewAccount(@Valid @RequestBody User user) {
        return ResponseEntity.ok(authService.createNewAccount(user));
    }

}