package com.example.demo.dto;

import com.example.demo.model.Projet;
import com.example.demo.model.User;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class ProjetResponseDto {
    private Long id_projet;
    private String name;
    private String description;
    private String ownerUsername;
    private String categorieName;
    private int taskCount;
    private List<String> collaboratorUsernames;

    public ProjetResponseDto(Projet projet, int taskCount) {
        this.id_projet = projet.getId_projet();
        this.name = projet.getName();
        this.description = projet.getDescription();
        this.ownerUsername = projet.getOwner().getUsername();
        this.categorieName = projet.getCategorie().getName_categorie();
        this.taskCount = taskCount;
        this.collaboratorUsernames = projet.getCollaborators().stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
    }
}