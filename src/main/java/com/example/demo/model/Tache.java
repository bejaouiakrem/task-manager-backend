package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Tache extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_tache;
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    // Many tasks → one project
    @ManyToOne
    @JoinColumn(name = "projet_id")
    private Projet projet;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner; // 👈 person responsible for this task
}
