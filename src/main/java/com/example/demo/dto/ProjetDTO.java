package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjetDTO {
    private Long id_projet;
    private String name;
    private String description;
    private Long categorieId;
}

