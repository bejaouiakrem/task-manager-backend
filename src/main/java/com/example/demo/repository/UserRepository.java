package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.username <> :username")
    List<User> findAllExceptUsername(@Param("username") String username);

    // CORRECTED: Find available users for project (not collaborators and not owner)
    // Find available users for project (excluding admins and existing collaborators)
    @Query("SELECT u FROM User u WHERE u.id_user NOT IN " +
            "(SELECT c.id_user FROM Projet p JOIN p.collaborators c WHERE p.id_projet = :projectId) " +
            "AND u.id_user != :ownerId " +
            "AND u.role != 'ADMIN' " +
            "AND u.enabled = true")
    List<User> findAvailableUsersForProject(@Param("projectId") Long projectId, @Param("ownerId") Long ownerId);
    // CORRECTED: Find collaborators by project ID (simpler and correct)
    @Query("SELECT u FROM Projet p JOIN p.collaborators u WHERE p.id_projet = :projectId")
    List<User> findCollaboratorsByProjectId(@Param("projectId") Long projectId);
}