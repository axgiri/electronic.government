package axgiri.github.TaskService.service;

import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import axgiri.github.TaskService.handler.HandlerImpl.TaskHandlerImpl;
import axgiri.github.TaskService.model.Project;
import axgiri.github.TaskService.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProjectService {

    private final ProjectRepository repository;
    private final ObjectMapper objectMapper;
    private final TaskHandlerImpl taskHandlerImpl;

    public Flux<Project> get() {
        log.info("fetching all projects");
        return repository.findAll()
            .doOnComplete(() -> log.info("completed fetching all projects"))
            .doOnError(e -> log.error("error fetching all projects", e));
    }

    public Mono<Project> getById(Long id) {
        log.info("fetching project with id: {}", id);
        return repository.findById(id)
            .doOnNext(project -> log.info("found project with id: {}", id))
            .switchIfEmpty(Mono.defer(() -> {
                log.error("froject not found with id: {}", id);
                return Mono.error(new RuntimeException("project not found"));
            }))
            .doOnError(e -> log.error("error fetching project with id: {}", id, e));
    }

    public Mono<List<Long>> getUsers(Long projectId) {
        log.info("fetching users for project with id: {}", projectId);
        return repository.findById(projectId)
            .switchIfEmpty(Mono.defer(() -> {
                log.error("project not found with id: {}", projectId);
                return Mono.error(new RuntimeException("project not found"));
            }))
            .flatMap(project -> {
                try {
                    List<Long> users = project.getUsersIdAsList(objectMapper);
                    log.info("found users for project {}: {}", projectId, users);
                    return Mono.just(users);
                } catch (Exception e) {
                    log.error("failed to parse usersId JSON for project with id: {}", projectId, e);
                    return Mono.error(new RuntimeException("failed to parse usersId JSON", e));
                }
            });
    }

    public Mono<Project> removeUsers(Long projectId, List<Long> userIds) {
        log.info("removing users {} from project with id: {}", userIds, projectId);
        return repository.findById(projectId)
            .switchIfEmpty(Mono.defer(() -> {
                log.error("project not found with id: {}", projectId);
                return Mono.error(new RuntimeException("project not found"));
            }))
            .flatMap(project -> {
                try {
                    List<Long> existingUsers = project.getUsersIdAsList(objectMapper);
                    if (existingUsers != null && !existingUsers.isEmpty()) {
                        existingUsers.removeAll(userIds);
                        project.setUsersIdAsList(existingUsers, objectMapper);
                        log.info("users {} removed from project {}. Updated users list: {}", userIds, projectId, existingUsers);
                    } else {
                        log.info("no users found in project with id: {} to remove", projectId);
                    }
                    return repository.save(project)
                        .doOnSuccess(updated -> log.info("project updated successfully after removing users. Project id: {}", projectId));
                } catch (Exception e) {
                    log.error("failed to update usersId JSON for project with id: {}", projectId, e);
                    return Mono.error(new RuntimeException("failed to update usersId JSON", e));
                }
            });
    }
    
    public Mono<Project> addUsers(Long projectId, List<Long> userIds) {
        log.info("adding users {} to project with id: {}", userIds, projectId);
        return repository.findById(projectId)
            .switchIfEmpty(Mono.defer(() -> {
                log.error("project not found with id: {}", projectId);
                return Mono.error(new RuntimeException("project not found"));
            }))
            .flatMap(project -> {
                try {
                    List<Long> existingUsers = Optional.ofNullable(project.getUsersIdAsList(objectMapper))
                            .orElse(new ArrayList<>());
                    userIds.stream()
                            .filter(userId -> !existingUsers.contains(userId))
                            .forEach(existingUsers::add);
                    project.setUsersIdAsList(existingUsers, objectMapper);
                    log.info("users {} added to project {}. Updated users list: {}", userIds, projectId, existingUsers);
                    return repository.save(project)
                        .doOnSuccess(updated -> log.info("project updated successfully after adding users. Project id: {}", projectId));
                } catch (Exception e) {
                    log.error("failed to update users list for project with id: {}", projectId, e);
                    return Mono.error(new RuntimeException("failed to update users list", e));
                }
            });
    }

    public Mono<Project> create(Project project) {
        log.info("creating new project with name: {}", project.getName());
        return repository.save(project)
            .doOnSuccess(created -> log.info("project created successfully with id: {}", created.getId()))
            .doOnError(e -> log.error("error creating project", e));
    }

    public Mono<Project> update(Long id, Project project) {
        log.info("updating project with id: {}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("project not found with id: {}", id);
                    return Mono.error(new RuntimeException("project not found"));
                }))
                .flatMap(existingProject -> {
                    try {
                        existingProject.setName(project.getName());
                        existingProject.setDescription(project.getDescription());
                        existingProject.setUsersId(objectMapper.writeValueAsString(project.getUsersId()));
                        return repository.save(existingProject)
                            .doOnSuccess(updated -> log.info("project with id: {} updated successfully", id));
                    } catch (JsonProcessingException e) {
                        log.error("failed to convert usersId to JSON for project with id: {}", id, e);
                        return Mono.error(new RuntimeException("failed to convert usersId to JSON", e));
                    }
                });
    }

    public Mono<Void> delete(Long projectId) {
        log.info("deleting project with id: {}", projectId);
        return repository.findById(projectId)
            .switchIfEmpty(Mono.defer(() -> {
                log.error("project not found with id: {}", projectId);
                return Mono.error(new RuntimeException("project not found"));
            }))
            .flatMap(project -> {
                log.info("deleting tasks associated with project id: {}", projectId);
                taskHandlerImpl.deleteTasksByProjectId(projectId);
                return repository.deleteById(projectId);
            })
            .doOnError(e -> log.error("error deleting project with id: {}", projectId, e));
    }
}
