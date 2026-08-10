package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserProfileResponse {
    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("enabled")
    private boolean enabled;

    @JsonProperty("projectCount")
    private int projectCount;

    @JsonProperty("role")
    private String role;

    public UserProfileResponse(String username, String email, boolean enabled, int projectCount, String role) {
        this.username = username;
        this.email = email;
        this.enabled = enabled;
        this.projectCount = projectCount;
        this.role = role;
    }
}