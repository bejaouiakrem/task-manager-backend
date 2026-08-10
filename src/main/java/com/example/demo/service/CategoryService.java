// CategoryService.java
package com.example.demo.service;

import com.example.demo.dto.CategoryDto;
import com.example.demo.model.Categorie;
import com.example.demo.repository.CategorieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategorieRepository categorieRepository;

    public List<CategoryDto> getAllCategories() {
        return categorieRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private CategoryDto convertToDto(Categorie categorie) {
        CategoryDto dto = new CategoryDto();
        dto.setId(categorie.getId_categorie());
        dto.setName(categorie.getName_categorie());
        return dto;
    }
}