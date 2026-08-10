package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_user;

    private String username;
    private String password;
    private String email;
    private boolean enabled;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.USER;

    // 👇 Removed bidirectional relation to Projet and Tache to avoid cycles
}
