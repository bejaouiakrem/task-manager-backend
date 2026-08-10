package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ProjetRequestDto {
    @NotBlank(message = "Project name is required")
    @Size(min = 3, max = 100, message = "Project name must be between 3-100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Category ID is required")
    private Long categorieId;

    private List<Long> collaboratorIds;
}