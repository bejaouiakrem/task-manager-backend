package com.example.demo.repository;

import com.example.demo.model.Projet;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjetRepository extends JpaRepository<Projet, Long> {
    int countByOwnerOrCollaboratorsContaining(User owner, User collaborator);

    // Option 1: Using explicit JPQL query
    @Query("SELECT p FROM Projet p WHERE p.owner.id_user = :ownerId")
    List<Projet> findByOwnerId(@Param("ownerId") Long ownerId);



    // For collaborators
    @Query("SELECT p FROM Projet p JOIN p.collaborators c WHERE c.id_user = :userId")
    List<Projet> findByCollaboratorsId(@Param("userId") Long userId);

    @Query("SELECT COUNT(t) FROM Tache t WHERE t.projet.id_projet = :projectId")
    int countTasksByProjectId(@Param("projectId") Long projectId);


    @Query("SELECT DISTINCT p FROM Projet p JOIN FETCH p.collaborators c WHERE c = :user ")
    List<Projet> findByCollaboratorsContainingWithFetch(@Param("user") User user);

}
