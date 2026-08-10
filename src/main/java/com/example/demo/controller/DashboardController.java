package com.example.demo.controller;

import com.example.demo.dto.NewProjectResponse;
import com.example.demo.model.Projet;
import com.example.demo.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/new-projects")
    public ResponseEntity<List<NewProjectResponse>> getNewProjects(Principal principal) {
        List<Projet> newProjects = dashboardService.getNewProjectsForUser(principal.getName());

        List<NewProjectResponse> responses = newProjects.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    private NewProjectResponse convertToResponse(Projet projet) {
        return NewProjectResponse.builder()
                .id(projet.getId_projet())
                .name(projet.getName())
                .description(projet.getDescription())
                .ownerUsername(projet.getOwner().getUsername())
                .assignedDate(getAssignmentDate(projet)) // You might need to track this
                .totalTasks(projet.getTaches() != null ? projet.getTaches().size() : 0)
                .yourTasks(0) // User hasn't created tasks yet
                .build();
    }

    private LocalDateTime getAssignmentDate(Projet projet) {
        // You might want to track when user was added as collaborator
        // For now, return project creation date
        return projet.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime();    }
}