package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NewProjectResponse {
    private Long id;
    private String name;
    private String description;
    private String ownerUsername;
    private LocalDateTime assignedDate;
    private int totalTasks; // Total tasks in project
    private int yourTasks; // Tasks assigned to this user
}
