package com.example.demo.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String username;
    private String email;
    private boolean enabled;
    private String role;
}
