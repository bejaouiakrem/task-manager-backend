package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Categorie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_categorie;
    private String name_categorie;

    @OneToMany(mappedBy = "categorie", cascade = CascadeType.ALL)
    private List<Projet> projets;
}
