package axgiri.github.TaskService.Controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import axgiri.github.TaskService.DTO.ProjectDTO;
import axgiri.github.TaskService.Service.ProjectService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public Flux<ProjectDTO> getAllProjects() {
        return projectService.getAllProjects();
    }

    @GetMapping("/{id}")
    public Mono<ProjectDTO> getProjectById(@PathVariable Long id) {
        return projectService.getProjectById(id);
    }

    @PostMapping
    public Mono<ProjectDTO> createProject(@RequestBody ProjectDTO projectDTO) {
        return projectService.createProject(projectDTO);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteProject(@PathVariable Long id) {
        return projectService.deleteProject(id);
    }
}
