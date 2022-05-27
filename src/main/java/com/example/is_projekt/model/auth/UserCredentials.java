package com.example.project.model.auth;

import com.sun.istack.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCredentials {

    @NotNull
    private String username;

    @NotNull
    private String password;
}