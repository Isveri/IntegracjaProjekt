package com.example.is_projekt.services;


import com.example.is_projekt.model.User;

import com.example.is_projekt.model.auth.TokenResponse;
import com.example.project.model.auth.UserCredentials;

public interface AuthService {

    TokenResponse getToken(UserCredentials userCredentials);

    TokenResponse createNewAccount(User user);

}
