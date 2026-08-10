package com.example.demo.service;

import com.example.demo.model.Projet;
import com.example.demo.model.User;
import com.example.demo.repository.ProjetRepository;
import com.example.demo.repository.TacheRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DashboardService {

    @Autowired
    private ProjetRepository projetRepository;

    @Autowired
    private TacheRepository tacheRepository;

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);

    public List<Projet> getNewProjectsForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        logger.info("User found: {} (ID: {})", username, user.getId_user());

        // Get all projects where user is collaborator
        List<Projet> collaboratorProjects = projetRepository.findByCollaboratorsContainingWithFetch(user);
        logger.info("Found {} collaborator projects", collaboratorProjects.size());

        // Debug: Check each project's collaborators
        for (Projet project : collaboratorProjects) {
            logger.info("Project: {} (ID: {})", project.getName(), project.getId_projet());
            logger.info("Project collaborators count: {}",
                    project.getCollaborators() != null ? project.getCollaborators().size() : "null");

            // Check if user is actually in collaborators list
            if (project.getCollaborators() != null) {
                boolean userIsCollaborator = project.getCollaborators().stream()
                        .anyMatch(collab -> collab.getId_user().equals(user.getId_user()));
                logger.info("User is in collaborators list: {}", userIsCollaborator);
            }
        }

        // Filter projects where user hasn't created any tasks
        List<Projet> newProjects = collaboratorProjects.stream()
                .filter(project -> {
                    boolean hasTasks = hasUserCreatedTasksInProject(user, project);
                    logger.info("Project {}: user has tasks? {}", project.getName(), hasTasks);
                    return !hasTasks;
                })
                .collect(Collectors.toList());

        logger.info("Final result: {} new projects", newProjects.size());
        return newProjects;
    }

    private boolean hasUserCreatedTasksInProject(User user, Projet project) {
        // Method 1: Using the query method
        return tacheRepository.hasUserCreatedTasksInProject(project, user);

        // Method 2: Check if any tasks exist
        // return !tacheRepository.findTasksByUserInProject(project, user).isEmpty();
    }
}
