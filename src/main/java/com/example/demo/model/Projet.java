package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Projet extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_projet;

    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner; // 👈 the creator of the project

    @ManyToMany
    @JoinTable(
            name = "projet_collaborateurs",
            joinColumns = @JoinColumn(name = "projet_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> collaborators; //

    @ManyToOne
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;

    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL)
    private List<Tache> taches;
}
