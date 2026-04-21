package com.umcsuser.carrent.models;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String id;
    private String login;
    private String password;
    private Role role;

    public User copy() {
        return new User(id, login, password, role);
    }
}