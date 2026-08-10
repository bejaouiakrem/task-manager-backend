package com.example.demo.controller;

import com.example.demo.dto.TaskDTO;
import com.example.demo.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/project/{projectId}")
    public List<TaskDTO> getTasksByProjectId(@PathVariable Long projectId) {
        return taskService.getTasksByProjectId(projectId);
    }

    // Create a new task
    @PostMapping
    public TaskDTO createTask(@RequestBody TaskDTO taskDTO) {
        return taskService.createTask(taskDTO);
    }
    // Update an existing task
    @PutMapping("/{id}")
    public TaskDTO updateTask(@PathVariable Long id, @RequestBody TaskDTO taskDTO) {
        return taskService.updateTask(id, taskDTO);
    }

    // Delete a task
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }

    // (Optional) Get a single task by its ID
    @GetMapping("/{id}")
    public TaskDTO getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }
}
