package axgiri.github.TaskService.Service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import axgiri.github.TaskService.DTO.ProjectDTO;
import axgiri.github.TaskService.Repository.ProjectRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ObjectMapper objectMapper;

    public ProjectService(ProjectRepository projectRepository, ObjectMapper objectMapper) {
        this.projectRepository = projectRepository;
        this.objectMapper = objectMapper;
    }

    public Flux<ProjectDTO> getAllProjects() {
        return projectRepository.findAll()
            .map(project -> ProjectDTO.fromEntityToDTO(project, objectMapper));
    }

    public Mono<ProjectDTO> getProjectById(Long id) {
        return projectRepository.findById(id)
            .map(project -> ProjectDTO.fromEntityToDTO(project, objectMapper));
    }

    public Mono<ProjectDTO> createProject(ProjectDTO projectDTO) {
        return projectRepository.save(projectDTO.toEntity(objectMapper))
            .map(project -> ProjectDTO.fromEntityToDTO(project, objectMapper));
    }

    public Mono<Void> deleteProject(Long id) {
        return projectRepository.deleteById(id);
    }
}
