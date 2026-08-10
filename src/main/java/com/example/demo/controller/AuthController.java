package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.exceptions.GestionTacheException;
import com.example.demo.service.AuthService;
import com.example.demo.service.RefreshTokensService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@Tag(
        name = "Authentication API",
        description = "Endpoints for user authentication and account management"
)
public class AuthController {

    private final RefreshTokensService refreshTokensService;
    @Value("${send.email.verificationUrl}")
    private String verificationUrl;

    @Value("${ResetPassword.email.verificationUrl}")
    private String ResetUrl;

    private final AuthService authService;

    public AuthController(AuthService authService, RefreshTokensService refreshTokensService) {
        this.authService = authService;
        this.refreshTokensService = refreshTokensService;
    }

    @Operation(
            summary = "User Registration",
            description = "Register a new user account. Verification email will be sent.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User registration details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RegisterRequest.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Registration successful - verification email sent",
                            content = @Content(schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Email or username already in use"
                    )
            }
    )
    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>>  signup(@RequestBody RegisterRequest registerRequest) {
        try {
            authService.signup(registerRequest, verificationUrl);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("message", "Registration Successful"));
        } catch (GestionTacheException e) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Account Verification",
            description = "Verify user account using token sent via email",
            parameters = {
                    @Parameter(
                            name = "token",
                            description = "Verification token sent to user's email",
                            required = true,
                            in = ParameterIn.PATH
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Account activated successfully",
                            content = @Content(schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid verification token"
                    )
            }
    )
    @GetMapping(value = "accountVerification/{token}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> verifyAccount(@PathVariable String token) {
        authService.verifyAccount(token);
        return ResponseEntity.ok()
                .body(Map.of("message", "Account Activated Successfully"));
    }

    @Operation(
            summary = "User Login",
            description = "Authenticate user and return JWT tokens",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User credentials",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequest.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Authentication successful",
                            content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Invalid credentials or account not activated"
                    )
            }
    )
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(authService.login(loginRequest));
    }


    @PostMapping("/forgot-password")
    @Operation(
            summary = "Password Reset Request",
            description = "Initiate password reset process. Reset link will be sent to email.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User email address",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PasswordResetRequest.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Reset link sent to email",
                            content = @Content(schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Email not found"
                    )
            }
    )
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody PasswordResetRequest request) {
        try {

            authService.requestPasswordReset(request.getEmail(), ResetUrl);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("message", "Password reset link sent to your email"));
        } catch (GestionTacheException e) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", e.getMessage()));
        }
    }


    @PostMapping("/reset-password/{token}")
    @Operation(
            summary = "Password Reset",
            description = "Reset user password using token from email",
            parameters = {
                    @Parameter(
                            name = "token",
                            description = "Password reset token sent to user's email",
                            required = true,
                            in = ParameterIn.PATH
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "New password details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = NewPasswordRequest.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Password reset successful",
                            content = @Content(schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid reset token"
                    )
            }
    )
    public ResponseEntity<Map<String, String>> resetPassword(
            @PathVariable String token,
            @RequestBody NewPasswordRequest request) {
        try {
            authService.resetPassword(token, request.getNewPassword());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("message", "Password has been reset successfully"));
        } catch (GestionTacheException e) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", e.getMessage()));
        }
    }


    @PostMapping("/refresh/token")
    @Operation(
            summary = "Refresh Access Token",
            description = "Generate new access token using refresh token",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Refresh token details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RefreshTokenRequest.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "New tokens generated",
                            content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Invalid refresh token"
                    )
            }
    )
    public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(authService.refreshToken(request));
    }


    @PostMapping("/logout")
    @Operation(
            summary = "User Logout",
            description = "Invalidate refresh token (logout user)",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Refresh token to invalidate",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RefreshTokenRequest.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Refresh token invalidated"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Invalid token"
                    )
            }
    )
    public ResponseEntity<Map<String, String>> logout(@RequestBody RefreshTokenRequest request) {
        refreshTokensService.deleteRefreshToken(request.getRefreshToken());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("message", "Successfully logged out"));
    }
}