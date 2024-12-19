package com.libmanage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.libmanage.model.User;

public class UserDTO {

    @JsonProperty("userId")
    Integer userId;

    @JsonProperty("username")
    String username;

    @JsonProperty("role")
    String role;

    public UserDTO() {
    }

    public UserDTO(Integer id, String role, String username) {
        this.userId = id;
        this.role = role;
        this.username = username;
    }

    public static UserDTO fromEntity(User user) {
        return new UserDTO(
                user.id(),
                user.getUsername(),
                user.getRole().getRoleName()
        );
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getId() {
        return userId;
    }

    public void setId(Integer id) {
        this.userId = id;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
