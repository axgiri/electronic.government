package axgiri.github.TaskService.Controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import axgiri.github.TaskService.DTO.TaskDTO;
import axgiri.github.TaskService.Service.TaskService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/tasks")
@Validated
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping
    public Flux<TaskDTO> getAllTasks() {
        return service.get();
    }

    @GetMapping("/members/{id}")
    public Mono<TaskDTO> getTaskById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/members/user/{userId}")
    public Flux<TaskDTO> getTasksByUserId(@PathVariable Long userId) {
        return service.getByUserId(userId);
    }
    
    @GetMapping("/members/project/{projectId}")
    public Flux<TaskDTO> getTasksByProject(@PathVariable Long projectId) {
        return service.getByProjectId(projectId);
    }

    @PutMapping("/moderators/{id}")
    public Mono<TaskDTO> updateTask(@PathVariable Long id, @RequestBody TaskDTO taskDTO) {
        return service.update(id, taskDTO);
    }

    @PostMapping("/members")
    public Mono<TaskDTO> createTask(@RequestBody TaskDTO taskDTO) {
        return service.create(taskDTO);
    }

    @DeleteMapping("/moderators/{id}")
    public Mono<Void> deleteTask(@PathVariable Long id) {
        return service.delete(id);
    }
}
