package axgiri.github.TaskService.Service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import axgiri.github.TaskService.DTO.ProjectDTO;
import axgiri.github.TaskService.Repository.ProjectRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProjectService {

    private final ProjectRepository repository;
    private final ObjectMapper objectMapper;

    public ProjectService(ProjectRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public Flux<ProjectDTO> get() {
        return repository.findAll()
            .map(project -> ProjectDTO.fromEntityToDTO(project, objectMapper));
    }

    public Mono<ProjectDTO> getById(Long id) {
        return repository.findById(id)
            .switchIfEmpty(Mono.error(new RuntimeException("project not found")))
            .map(project -> ProjectDTO.fromEntityToDTO(project, objectMapper));
    }

    public Mono<ProjectDTO> create(ProjectDTO projectDTO) {
        return repository.save(projectDTO.toEntity(objectMapper))
            .map(project -> ProjectDTO.fromEntityToDTO(project, objectMapper));
    }

    public Mono<ProjectDTO> update(Long id, ProjectDTO projectDTO) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("project not found")))
                .flatMap(existingProject -> {
                    try {
                        existingProject.setName(projectDTO.getName());
                        existingProject.setDescription(projectDTO.getDescription());
                        existingProject.setUsersId(objectMapper.writeValueAsString(projectDTO.getUsersId()));
                        return repository.save(existingProject);
                    } catch (JsonProcessingException e) {
                        return Mono.error(new RuntimeException("failed to convert usersId to JSON", e));
                    }
                })                
                .map(updatedProject -> ProjectDTO.fromEntityToDTO(updatedProject, objectMapper));
    }
    

    public Mono<Void> delete(Long id) {
        return repository.deleteById(id);
    }
}
