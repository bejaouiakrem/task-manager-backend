package com.example.demo.dto;

import com.example.demo.model.User;
import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private boolean enabled;
    private String role;

    // Add this constructor for easy conversion
    public UserResponse(Long id, String username, String email, boolean enabled, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.enabled = enabled;
        this.role = role;
    }

    // Optional: Add a static factory method
    public static UserResponse fromUser(User user) {
        return new UserResponse(
                user.getId_user(),
                user.getUsername(),
                user.getEmail(),
                user.isEnabled(),
                user.getRole().name()
        );
    }
}