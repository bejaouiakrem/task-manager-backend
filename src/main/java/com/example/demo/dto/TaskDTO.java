package com.example.demo.dto;

import com.example.demo.model.Priority;
import com.example.demo.model.Status;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskDTO {
    private Long id_tache;
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    private Long projectId;
    private String projectName;
    private String ownerUsername;
}
