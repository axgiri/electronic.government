package axgiri.github.TaskService.Service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import axgiri.github.TaskService.DTO.ProjectDTO;
import axgiri.github.TaskService.Repository.ProjectRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public Mono<List<Long>> getUsers(Long projectId) {
        return repository.findById(projectId)
            .switchIfEmpty(Mono.error(new RuntimeException("project not found")))
            .flatMap(project -> {
                try {
                    return Mono.just(project.getUsersIdAsList(objectMapper));
                } catch (Exception e) {
                    return Mono.error(new RuntimeException("failed to parse usersId JSON", e));
                }
            });
    }

    public Mono<ProjectDTO> removeUsers(Long projectId, List<Long> userIds) {
        return repository.findById(projectId)
            .switchIfEmpty(Mono.error(new RuntimeException("project not found")))
            .flatMap(project -> {
                try {
                    List<Long> existingUsers = project.getUsersIdAsList(objectMapper);
                    if (existingUsers != null && !existingUsers.isEmpty()) {
                        userIds.forEach(existingUsers::remove);
                        project.setUsersIdAsList(existingUsers, objectMapper);
                    }
                    return repository.save(project);
                } catch (Exception e) {
                    return Mono.error(new RuntimeException("failed to update usersId JSON", e));
                }
            })
            .map(updatedProject -> ProjectDTO.fromEntityToDTO(updatedProject, objectMapper));
    }
    
    public Mono<ProjectDTO> addUsers(Long projectId, List<Long> userIds) {
        return repository.findById(projectId)
            .switchIfEmpty(Mono.error(new RuntimeException("project not found")))
            .flatMap(project -> {
                try {
                    List<Long> existingUsers = Optional.ofNullable(project.getUsersIdAsList(objectMapper))
                            .orElse(new ArrayList<>());
                    userIds.stream()
                            .filter(userId -> !existingUsers.contains(userId))
                            .forEach(existingUsers::add);
                    project.setUsersIdAsList(existingUsers, objectMapper);
                    return repository.save(project);
                } catch (Exception e) {
                    return Mono.error(new RuntimeException("failed to update users list", e));
                }
            })
            .map(updatedProject -> ProjectDTO.fromEntityToDTO(updatedProject, objectMapper));
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
