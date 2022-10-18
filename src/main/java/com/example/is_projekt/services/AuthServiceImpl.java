package com.example.is_projekt.services;


import com.example.is_projekt.exceptions.InvalidCredentialsException;
import com.example.is_projekt.exceptions.UserNotFoundException;
import com.example.is_projekt.model.User;
import com.example.is_projekt.model.auth.TokenResponse;
import com.example.is_projekt.repositories.UserRepository;
import com.example.is_projekt.security.JwtTokenUtil;
import com.example.project.model.auth.UserCredentials;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.CredentialNotFoundException;


@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {


    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public TokenResponse getToken(UserCredentials userCredentials) {
        User user = (User) userDetailsService.loadUserByUsername(userCredentials.getUsername());
        if (passwordEncoder.matches(userCredentials.getPassword(), user.getPassword()))
            return new TokenResponse(jwtTokenUtil.generateAccessToken(user));
        throw new InvalidCredentialsException();
    }

    @Override
    public TokenResponse createNewAccount(User user) {

        String username = user.getUsername();
        String password = user.getPassword();
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserNotFoundException();
        } else {
            user.setPassword(passwordEncoder.encode(password));
            user.setUsername(username);
           userRepository.save(user);

            return new TokenResponse(jwtTokenUtil.generateAccessToken(user));
        }
    }

}
