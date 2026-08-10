package com.example.demo.service;

import com.example.demo.dto.TaskDTO;
import com.example.demo.model.Tache;
import com.example.demo.repository.ProjetRepository;
import com.example.demo.repository.TacheRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {


    private final TacheRepository taskRepository;
    private final ProjetRepository projetRepository;
    private final UserRepository userRepository;

    public List<TaskDTO> getTasksByProjectId(Long projectId) {
        return taskRepository.findByProjectId(projectId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private TaskDTO convertToDTO(Tache task) {
        return TaskDTO.builder()
                .id_tache(task.getId_tache())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .projectId(task.getProjet().getId_projet())
                .projectName(task.getProjet().getName())
                .ownerUsername(task.getOwner() != null ? task.getOwner().getUsername() : null)
                .build();
    }

    public TaskDTO createTask(TaskDTO taskDTO) {
        Tache task = new Tache();
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(taskDTO.getStatus());
        task.setPriority(taskDTO.getPriority());

        // 🧩 Set linked project
        task.setProjet(projetRepository.findById(taskDTO.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found")));

        // 🧑‍💻 Set task owner
        if (taskDTO.getOwnerUsername() != null) {
            task.setOwner(userRepository.findByUsername(taskDTO.getOwnerUsername())
                    .orElseThrow(() -> new RuntimeException("Owner user not found")));
        }

        Tache saved = taskRepository.save(task);
        return convertToDTO(saved);
    }


    public TaskDTO updateTask(Long id, TaskDTO taskDTO) {
        Tache task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(taskDTO.getStatus());
        task.setPriority(taskDTO.getPriority());

        // 🔄 Update project
        task.setProjet(projetRepository.findById(taskDTO.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found")));

        // 🔄 Update owner
        if (taskDTO.getOwnerUsername() != null) {
            task.setOwner(userRepository.findByUsername(taskDTO.getOwnerUsername())
                    .orElseThrow(() -> new RuntimeException("Owner user not found")));
        }

        Tache saved = taskRepository.save(task);
        return convertToDTO(saved);
    }


    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public TaskDTO getTaskById(Long id) {
        Tache task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return convertToDTO(task);
    }
}
