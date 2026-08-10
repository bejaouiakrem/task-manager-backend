package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.exceptions.GestionTacheException;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjetService {

    private final ProjetRepository projetRepository;
    private final UserRepository userRepository;
    private final CategorieRepository categorieRepository;

    @Transactional
    public ProjetResponseDto createProjet(ProjetRequestDto dto, String username) {
        // Get current user by username
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Validate role
        if (!currentUser.getRole().equals(Role.ADMIN) &&
                !currentUser.getRole().equals(Role.PROJECT_MANAGER)) {
            throw new GestionTacheException("User doesn't have permission to create projects");
        }

        // Validate required fields
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("Project name is required");
        }

        if (dto.getCategorieId() == null) {
            throw new IllegalArgumentException("Category is required");
        }

        // Validate category exists
        Categorie category = categorieRepository.findById(dto.getCategorieId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        // Create project
        Projet projet = new Projet();
        projet.setName(dto.getName().trim());
        projet.setDescription(dto.getDescription() != null ?
                dto.getDescription().trim() : null);
        projet.setOwner(currentUser);
        projet.setCategorie(category);

        // Set collaborators (including owner)
        Set<User> collaborators = new HashSet<>();
        collaborators.add(currentUser); // Add owner as collaborator

        if (dto.getCollaboratorIds() != null && !dto.getCollaboratorIds().isEmpty()) {
            List<User> additionalCollaborators = userRepository.findAllById(dto.getCollaboratorIds());

            // Validate all collaborator IDs exist
            if (additionalCollaborators.size() != dto.getCollaboratorIds().size()) {
                throw new EntityNotFoundException("One or more collaborators not found");
            }

            collaborators.addAll(additionalCollaborators);
        }
        projet.setCollaborators(new ArrayList<>(collaborators));

        Projet savedProjet = projetRepository.save(projet);
        int taskCount = projetRepository.countTasksByProjectId(savedProjet.getId_projet());
        return new ProjetResponseDto(savedProjet, taskCount);
    }

    public List<ProjetResponseDto> getProjectsForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Projet> ownedProjects = projetRepository.findByOwnerId(user.getId_user());
        List<Projet> collaboratedProjects = projetRepository.findByCollaboratorsId(user.getId_user());

        Set<Projet> allProjects = new HashSet<>(ownedProjects);
        allProjects.addAll(collaboratedProjects);

        return allProjects.stream()
                .map(projet -> {
                    int taskCount = projetRepository.countTasksByProjectId(projet.getId_projet());
                    return new ProjetResponseDto(projet, taskCount);
                })
                .collect(Collectors.toList());
    }

    public List<UserSummaryDto> getAvailableUsers() {
        return userRepository.findAll().stream()
                .map(UserSummaryDto::new)
                .collect(Collectors.toList());
    }

    public int countUserProjects(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return projetRepository.countByOwnerOrCollaboratorsContaining(user, user);
    }

    public void deleteProject(Long id) {
        if (!projetRepository.existsById(id)) {
            throw new EntityNotFoundException("Project not found");
        }
        projetRepository.deleteById(id);
    }

    public ProjetDTO updateProject(Long id, ProjetDTO projetDTO) {
        Projet projet = projetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        projet.setName(projetDTO.getName());
        projet.setDescription(projetDTO.getDescription());

        if (projetDTO.getCategorieId() != null) {
            projet.setCategorie(categorieRepository.findById(projetDTO.getCategorieId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found")));
        }

        projetRepository.save(projet);
        return projetDTO; // Or map entity → DTO properly
    }


    public List<UserResponse> getProjectCollaborators(Long projectId) {
        List<User> collaborators = userRepository.findCollaboratorsByProjectId(projectId);

        // Convert User entities to UserResponse DTOs
        return collaborators.stream()
                .map(user -> new UserResponse(
                        user.getId_user(),
                        user.getUsername(),
                        user.getEmail(),
                        user.isEnabled(),
                        user.getRole().name()
                ))
                .collect(Collectors.toList());

    }

    public void addCollaborator(Long projectId, Long userId) {
        Projet projet = projetRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!projet.getCollaborators().contains(user)) {
            projet.getCollaborators().add(user);
            projetRepository.save(projet);
        }
    }

    public void removeCollaborator(Long projectId, Long userId) {
        Projet projet = projetRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        projet.getCollaborators().removeIf(user -> user.getId_user().equals(userId));
        projetRepository.save(projet);
    }
}