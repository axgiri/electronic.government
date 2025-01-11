package axgiri.github.TaskService.Controller;

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

import axgiri.github.TaskService.DTO.ProjectDTO;
import axgiri.github.TaskService.Service.ProjectService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/projects")
@Validated
public class ProjectController {

    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @GetMapping
    public Flux<ProjectDTO> getProjects() {
        return service.get();
    }

    @GetMapping("/{id}")
    public Mono<ProjectDTO> getProject(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/getUsers/{id}")
    public Mono<List<Long>> getUsersByProjectId(@PathVariable Long id) {
        return service.getUsers(id);
    }

    @PostMapping("/removeUsers/{id}")
    public Mono<ProjectDTO> removeUsers(@PathVariable Long id, @RequestBody List<Long> userIds) {
        return service.removeUsers(id, userIds);
    }

    @PutMapping("/addUsers/{id}")
    public Mono<ProjectDTO> addUsers(@PathVariable Long id, @RequestBody List<Long> userIds) {
        return service.addUsers(id, userIds);
    }

    @PostMapping
    public Mono<ProjectDTO> createProject(@RequestBody ProjectDTO projectDTO) {
        return service.create(projectDTO);
    }

    @PutMapping
    public Mono<ProjectDTO> update(@PathVariable Long id, @RequestBody ProjectDTO projectDTO) {
        return service.update(id, projectDTO);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteProject(@PathVariable Long id) {
        return service.delete(id);
    }
}
