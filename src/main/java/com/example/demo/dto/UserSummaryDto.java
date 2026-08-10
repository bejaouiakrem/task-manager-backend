package com.example.demo.dto;

import com.example.demo.model.User;
import lombok.Data;

@Data
public class UserSummaryDto {
    private Long id;
    private String username;
    private String email;
    private String role;

    public UserSummaryDto(User user) {
        this.id = user.getId_user();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole().name();
    }
}