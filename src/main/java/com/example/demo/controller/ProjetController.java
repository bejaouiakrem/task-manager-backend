package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.exceptions.GestionTacheException;
import com.example.demo.service.ProjetService;
import com.example.demo.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projets")
@RequiredArgsConstructor
public class ProjetController {

    private final ProjetService projetService;
    private final UserService userService;

    @PostMapping("/new")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PROJECT_MANAGER')")

    public ResponseEntity<?> createProjet(
            @Valid @RequestBody ProjetRequestDto dto,
            Authentication authentication
    ) {
        try {
            String username = authentication.getName();
            ProjetResponseDto newProjet = projetService.createProjet(dto, username);
            return ResponseEntity.status(HttpStatus.CREATED).body(newProjet);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (GestionTacheException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/my-projects")
    public ResponseEntity<List<ProjetResponseDto>> getUserProjects(Authentication authentication) {
        String username = authentication.getName();
        List<ProjetResponseDto> projects = projetService.getProjectsForUser(username);

        return ResponseEntity.ok(projects);
    }

    @GetMapping("/available-users")
    public ResponseEntity<List<UserSummaryDto>> getAvailableUsers() {
        List<UserSummaryDto> users = projetService.getAvailableUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> countUserProjects(Authentication authentication) {
        String username = authentication.getName();
        int count = projetService.countUserProjects(username);
        return ResponseEntity.ok(count);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROJECT_MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projetService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROJECT_MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<ProjetDTO> updateProject(
            @PathVariable Long id,
            @RequestBody ProjetDTO projetDTO) {
        ProjetDTO updated = projetService.updateProject(id, projetDTO);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{projectId}/available-users")
    public List<UserResponse> getAvailableUsersForProject(@PathVariable Long projectId) {
        // Users who are NOT collaborators and NOT the project owner
        return userService.getAvailableUsersForProject(projectId);
    }

    @GetMapping("/{projectId}/collaborators")
    public List<UserResponse> getProjectCollaborators(@PathVariable Long projectId) {
        return projetService.getProjectCollaborators(projectId);
    }

    // Add collaborator to project
    @PostMapping("/{projectId}/collaborators/{userId}")
    public ResponseEntity<?> addCollaborator(@PathVariable Long projectId, @PathVariable Long userId) {
        projetService.addCollaborator(projectId, userId);
        return ResponseEntity.ok().build();
    }

    // Remove collaborator from project
    @DeleteMapping("/{projectId}/collaborators/{userId}")
    public ResponseEntity<?> removeCollaborator(@PathVariable Long projectId, @PathVariable Long userId) {
        projetService.removeCollaborator(projectId, userId);
        return ResponseEntity.ok().build();
    }
}