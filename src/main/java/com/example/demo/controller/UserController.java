package com.example.demo.controller;

import com.example.demo.dto.UserProfileResponse;
import com.example.demo.dto.UserSummaryDto;
import com.example.demo.dto.UserUpdateRequest;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;


@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile() {
        UserProfileResponse response = userService.getCurrentUserProfile();
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(response);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        List<UserProfileResponse> users = userService.getAllUsersExceptCurrent();
        return ResponseEntity.ok(users);
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> updateUserProfile(
            @Valid @RequestBody UserUpdateRequest request) {
        try {
            userService.updateUserProfile(request);
            return ResponseEntity.ok()
                    .body(Collections.singletonMap("message", "User updated successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping(value = "/get-user/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserProfileResponse> getUserByUsername(@PathVariable String username) {
        UserProfileResponse response = userService.getUserByUsername(username);
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(response);
    }

    @DeleteMapping("/delete/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Collections.singletonMap("message", "User deleted successfully"));
    }

    @GetMapping("/available")
    public ResponseEntity<List<UserSummaryDto>> getAvailableUsers(Authentication authentication) {
        return ResponseEntity.ok(userService.getAvailableUsers(authentication));
    }

}