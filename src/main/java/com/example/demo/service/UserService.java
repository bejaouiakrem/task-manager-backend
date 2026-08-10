package com.example.demo.service;

import com.example.demo.dto.UserProfileResponse;
import com.example.demo.dto.UserResponse;
import com.example.demo.dto.UserSummaryDto;
import com.example.demo.dto.UserUpdateRequest;
import com.example.demo.model.Projet;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.ProjetRepository;
import com.example.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ProjetRepository projetRepository;
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        int projectCount = projetRepository.countByOwnerOrCollaboratorsContaining(user, user);

        return new UserProfileResponse(
                user.getUsername(),
                user.getEmail(),
                user.isEnabled(),
                projectCount,
                user.getRole().name()
        );
    }

    public List<UserProfileResponse> getAllUsersExceptCurrent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        return userRepository.findAllExceptUsername(currentUsername).stream()
                .map(user -> {
                    int projectCount = projetRepository.countByOwnerOrCollaboratorsContaining(user, user);
                    return UserProfileResponse.builder()
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .enabled(user.isEnabled())
                            .projectCount(projectCount)
                            .role(user.getRole().name())
                            .build();
                })
                .toList();
    }

    public void updateUserProfile(UserUpdateRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEmail(request.getEmail());
        user.setEnabled(request.isEnabled());
        user.setRole(Role.valueOf(request.getRole().toUpperCase()));

        userRepository.save(user);
    }


    public UserProfileResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        int totalProjectCount = projetRepository.countByOwnerOrCollaboratorsContaining(user, user);

        return UserProfileResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .enabled(user.isEnabled())
                .projectCount(totalProjectCount)
                .role(user.getRole().name())
                .build();
    }




    @Transactional
    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Count projects where the user is either an owner or collaborator
        int projectCount = projetRepository.countByOwnerOrCollaboratorsContaining(user, user);

        // Prevent deletion if the user is involved in any project
        if (projectCount > 0) {
            throw new IllegalStateException("Cannot delete user with active projects or collaborations.");
        }

        userRepository.delete(user);
    }

    public List<UserSummaryDto> getAvailableUsers(Authentication authentication) {
        String currentUsername = authentication.getName();

        return userRepository.findAll().stream()
                .filter(user -> !user.getUsername().equals(currentUsername)
                        && !user.getRole().equals(Role.ADMIN) )
                // Add other filters as needed
                .map(UserSummaryDto::new)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getAvailableUsersForProject(Long projectId) {
        Projet project = projetRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        List<User> availableUsers = userRepository.findAvailableUsersForProject(projectId, project.getOwner().getId_user());

        // Convert User entities to UserResponse DTOs
        return availableUsers.stream()
                .map(user -> new UserResponse(
                        user.getId_user(),
                        user.getUsername(),
                        user.getEmail(),
                        user.isEnabled(),
                        user.getRole().name()
                ))
                .collect(Collectors.toList());
    }

}