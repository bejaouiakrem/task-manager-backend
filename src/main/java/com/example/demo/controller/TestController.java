package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    // Public endpoint (no authentication required)
    @GetMapping("/hello")
    public ResponseEntity<String> publicHello() {
        return ResponseEntity.ok("Public hello - no authentication needed");
    }

    // Protected endpoint - requires valid JWT
    @GetMapping("/protected")
    public ResponseEntity<Map<String, String>> protectedEndpoint() {
        // Get authentication information from security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Extract user details
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Create response with user information
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello from protected endpoint!");
        response.put("username", userDetails.getUsername());
        response.put("authorities", userDetails.getAuthorities().toString());

        return ResponseEntity.ok(response);
    }

    // Endpoint to show current user details
    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("username", userDetails.getUsername());
        userInfo.put("authorities", userDetails.getAuthorities().toString());
        userInfo.put("authenticated", authentication.isAuthenticated() ? "true" : "false");

        return ResponseEntity.ok(userInfo);
    }
}