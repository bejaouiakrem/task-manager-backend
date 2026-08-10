package com.example.demo.repository;

import com.example.demo.model.Projet;
import com.example.demo.model.Tache;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TacheRepository extends JpaRepository<Tache, Long> {

    @Query("SELECT t FROM Tache t WHERE t.projet.id_projet = :projectId")
    List<Tache> findByProjectId(@Param("projectId") Long projectId);

    // Alternative: Use @Query for more control
    @Query("SELECT COUNT(t) > 0 FROM Tache t WHERE t.projet = :projet AND t.owner = :owner")
    boolean hasUserCreatedTasksInProject(@Param("projet") Projet projet, @Param("owner") User owner);

    // Or find all tasks by user in project
    @Query("SELECT t FROM Tache t WHERE t.projet = :projet AND t.owner = :owner")
    List<Tache> findTasksByUserInProject(@Param("projet") Projet projet, @Param("owner") User owner);}
