package axgiri.github.TaskService.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import axgiri.github.TaskService.request.TaskRequest;
import axgiri.github.TaskService.response.TaskResponse;
import axgiri.github.TaskService.service.TaskService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tasks")
@Validated
public class TaskController {

    private final TaskService service;

    @GetMapping
    public Flux<TaskResponse> getAllTasks() {
        return service.get()
            .map(TaskResponse::fromEntityToDTO);
    }

    @GetMapping("/members/{id}")
    public Mono<TaskResponse> getTaskById(@PathVariable Long id) {
        return service.getById(id)
            .map(TaskResponse::fromEntityToDTO);
    }

    @GetMapping("/members/user/{userId}")
    public Flux<TaskResponse> getTasksByUserId(@PathVariable Long userId) {
        return service.getByUserId(userId)
            .map(TaskResponse::fromEntityToDTO);
    }
    
    @GetMapping("/members/project/{projectId}")
    public Flux<TaskResponse> getTasksByProject(@PathVariable Long projectId) {
        return service.getByProjectId(projectId)
            .map(TaskResponse::fromEntityToDTO);
    }

    @PutMapping("/moderators/{id}")
    public Mono<TaskResponse> updateTask(@PathVariable Long id, @RequestBody TaskRequest taskRequest) {
        return service.update(id, taskRequest)
            .map(TaskResponse::fromEntityToDTO);
    }

    @PostMapping("/members")
    public Mono<TaskResponse> createTask(@RequestBody TaskRequest taskRequest) {
        return service.create(taskRequest)
            .map(TaskResponse::fromEntityToDTO);
    }

    @DeleteMapping("/moderators/{id}")
    public Mono<Void> deleteTask(@PathVariable Long id) {
        return service.delete(id);
    }
}
