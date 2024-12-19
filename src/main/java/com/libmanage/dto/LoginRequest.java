package com.libmanage.dto;


public class LoginRequest {

    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public LoginRequest(String password, String username) {
        this.password = password;
        this.username = username;
    }
}
