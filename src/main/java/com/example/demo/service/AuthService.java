package com.example.demo.service;

import com.example.demo.dto.AuthenticationResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RefreshTokenRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.exceptions.GestionTacheException;
import com.example.demo.model.*;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VerificationTokenRepository;
import com.example.demo.security.CustomUserDetailsService;
import com.example.demo.security.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;


@Service
@AllArgsConstructor

public class AuthService {


    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokensService refreshTokensService;


    @Transactional
    public void signup(RegisterRequest registerRequest , String verificationUrl) {
        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new GestionTacheException("Email already registered");
        }

        // Check if username already exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new GestionTacheException("Username already taken");
        }
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setCreatedAt(Instant.now());

        user.setRole(Role.USER);
        /*user.addRole(Role.USER);

        // Special cases:
        if (registerRequest.getEmail().endsWith("@yourcompany.com")) {
            user.addRole(Role.PROJECT_MANAGER);
        }*/

        user.setEnabled(false);

        userRepository.save(user);

        String token = generateVerificationToken(user);
        mailService.sendMail(new NotificationEmail("Please activate your account",
                user.getEmail(),"thank you for sign up , "+
                "please click on the below url to activate your account : "+verificationUrl+token));
    }

    private String generateVerificationToken(User user) {
        String token =  UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        verificationTokenRepository.save(verificationToken);

        return token;
    }
    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        verificationToken.orElseThrow(()-> new GestionTacheException("Invalid verification token"));
        fetchUserAndEnable(verificationToken.get());
    }


    protected void fetchUserAndEnable(VerificationToken verificationToken) {
        String username = verificationToken.getUser().getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(()-> new GestionTacheException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);

    }


    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;
    public AuthenticationResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwtToken = jwtUtil.generateToken(userDetails);
            Instant expiresAt = jwtUtil.extractExpiration(jwtToken).toInstant();

            RefreshToken refreshToken = refreshTokensService.generateRefreshTokens();

            String role = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .map(r -> r.replace("ROLE_", ""))
                    .orElse("USER");


            return AuthenticationResponse.builder()
                    .authenticationToken(jwtToken)
                    .refreshToken(refreshToken.getToken())
                    .expiresAt(expiresAt)
                    .username(userDetails.getUsername())
                    .role(role)
                    .build();

        } catch (DisabledException e) {
            throw new GestionTacheException("Account not activated. Check your email.");
        } catch (BadCredentialsException e) {
            throw new GestionTacheException("Invalid credentials");
        }
    }




    @Transactional
    public void requestPasswordReset(String email, String resetUrlBase) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GestionTacheException("Email not found"));


        String token = generateVerificationToken(user);
        String resetLink = resetUrlBase + token;
        mailService.sendMail(new NotificationEmail("Password Reset Request",
                user.getEmail(),"Click the link to reset your password : "+resetLink));


    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new GestionTacheException("Invalid token"));

        User user = verificationToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Optionally delete token after use
        verificationTokenRepository.delete(verificationToken);
    }


    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
        refreshTokensService.validateRefreshToken(request.getRefreshToken());
        String token = jwtUtil.generateToken(request.getUsername());

        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(request.getRefreshToken())
                .expiresAt(jwtUtil.extractExpiration(token).toInstant())
                .username(request.getUsername())
                .build();
    }
}
