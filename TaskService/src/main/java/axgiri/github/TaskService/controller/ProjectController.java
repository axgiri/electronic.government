package axgiri.github.TaskService.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import axgiri.github.TaskService.request.ProjectRequest;
import axgiri.github.TaskService.response.ProjectResponse;
import axgiri.github.TaskService.service.ProjectService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/projects")
@Validated
public class ProjectController {

    private final ProjectService service;
    private final ObjectMapper objectMapper;

    @GetMapping
    public Flux<ProjectResponse> getProjects() {
        return service.get()
            .map(project -> ProjectResponse.fromEntityToDTO(project, objectMapper));
    }

    @GetMapping("/members/{id}")
    public Mono<ProjectResponse> getProject(@PathVariable Long id) {
        return service.getById(id)
            .map(project -> ProjectResponse.fromEntityToDTO(project, objectMapper));
    }

    @GetMapping("/members/getUsers/{id}")
    public Mono<List<Long>> getUsersByProjectId(@PathVariable Long id) {
        return service.getUsers(id);
    }

    @PutMapping("/moderators/removeUsers/{id}")
    public Mono<ProjectResponse> removeUsers(@PathVariable Long id, @RequestBody List<Long> userIds) {
        return service.removeUsers(id, userIds)
            .map(project -> ProjectResponse.fromEntityToDTO(project, objectMapper));
    }

    @PutMapping("/moderators/addUsers/{id}")
    public Mono<ProjectResponse> addUsers(@PathVariable Long id, @RequestBody List<Long> userIds) {
        return service.addUsers(id, userIds)
            .map(project -> ProjectResponse.fromEntityToDTO(project, objectMapper));
    }

    @PostMapping("/admins")
    public Mono<ProjectResponse> createProject(@RequestBody ProjectRequest projectRequest) {
        return service.create(projectRequest.toEntity(objectMapper))
            .map(project -> ProjectResponse.fromEntityToDTO(project, objectMapper));
    }

    @PutMapping("/admins/{id}")
    public Mono<ProjectResponse> update(@PathVariable Long id, @RequestBody ProjectRequest projectRequest) {
        return service.update(id, projectRequest.toEntity(objectMapper))
            .map(project -> ProjectResponse.fromEntityToDTO(project, objectMapper));
    }

    @DeleteMapping("/admins/{id}")
    public Mono<Void> deleteProject(@PathVariable Long id) {
        return service.delete(id);
    }
}

