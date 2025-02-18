package axgiri.github.TaskService.service;

import org.springframework.stereotype.Service;
import axgiri.github.TaskService.model.Task;
import axgiri.github.TaskService.repository.TaskRepository;
import axgiri.github.TaskService.request.TaskRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class TaskService {

    private final TaskRepository repository;

    public Flux<Task> get() {
        log.info("fetching all tasks");
        return repository.findAll()
            .doOnComplete(() -> log.info("completed fetching all tasks"))
            .doOnError(e -> log.error("error fetching tasks", e));
    }

    public Mono<Task> getById(Long id) {
        log.info("fetching task with id: {}", id);
        return repository.findById(id)
            .doOnNext(task -> log.info("found task with id: {}", id))
            .switchIfEmpty(Mono.defer(() -> {
                log.error("task not found with id: {}", id);
                return Mono.error(new RuntimeException("task not found"));
            }))
            .doOnError(e -> log.error("error fetching task with id: {}", id, e));
    }

    public Flux<Task> getByUserId(Long userId) {
        log.info("fetching tasks for user with id: {}", userId);
        return repository.findByUserId(userId)
            .doOnComplete(() -> log.info("completed fetching tasks for user with id: {}", userId))
            .doOnError(e -> log.error("error fetching tasks for user with id: {}", userId, e));
    }

    public Flux<Task> getByProjectId(Long projectId) {
        log.info("fetching tasks for project with id: {}", projectId);
        return repository.findByProjectId(projectId)
            .doOnComplete(() -> log.info("completed fetching tasks for project with id: {}", projectId))
            .doOnError(e -> log.error("error fetching tasks for project with id: {}", projectId, e));
    }

    public Mono<Task> update(Long id, TaskRequest taskDTO) {
        log.info("updating task with id: {}", id);
        return repository.findById(id)
            .switchIfEmpty(Mono.defer(() -> {
                log.error("task not found with id: {}", id);
                return Mono.error(new RuntimeException("task not found"));
            }))
            .flatMap(existingTask -> {
                existingTask.setStatus(taskDTO.getStatus());
                existingTask.setUserId(taskDTO.getUserId());
                existingTask.setDescription(taskDTO.getDescription());
                return repository.save(existingTask)
                    .doOnSuccess(task -> log.info("task with id: {} updated successfully", id))
                    .doOnError(e -> log.error("error updating task with id: {}", id, e));
            });
    }

    public Mono<Task> create(TaskRequest taskDTO) {
        log.info("creating new task for project: {} and user: {}", taskDTO.getProjectId(), taskDTO.getUserId());
        return repository.save(taskDTO.toEntity())
            .doOnSuccess(task -> log.info("task created successfully with id: {}", task.getId()))
            .doOnError(e -> log.error("error creating task", e));
    }

    public Mono<Void> delete(Long id) {
        log.info("deleting task with id: {}", id);
        return repository.deleteById(id)
            .doOnSuccess(v -> log.info("task with id: {} deleted successfully", id))
            .doOnError(e -> log.error("error deleting task with id: {}", id, e));
    }
}
