package axgiri.github.TaskService.Service;

import org.springframework.stereotype.Service;

import axgiri.github.TaskService.DTO.TaskDTO;
import axgiri.github.TaskService.Repository.TaskRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public Flux<TaskDTO> get() {
        return repository.findAll()
            .map(TaskDTO::fromEntityToDTO);
    }

    public Mono<TaskDTO> getById(Long id) {
        return repository.findById(id)
            .map(TaskDTO::fromEntityToDTO);
    }

    public Flux<TaskDTO> getByUserId(Long userId) {
        return repository.findByUserId(userId)
            .map(TaskDTO::fromEntityToDTO);
    }

    public Flux<TaskDTO> getByProjectId(Long projectId){
        return repository.findByProjectId(projectId)
            .map(TaskDTO::fromEntityToDTO);
    }

    public Mono<TaskDTO> update(Long id, TaskDTO taskDTO){
        return repository.findById(id)
            .switchIfEmpty(Mono.error(new RuntimeException("task not found")))
            .flatMap(existingTask -> {
                existingTask.setStatus(taskDTO.getStatus());
                existingTask.setUserId(taskDTO.getUserId());
                existingTask.setDescription(taskDTO.getDescription());
                return repository.save(existingTask);
            })
            .map(TaskDTO::fromEntityToDTO);
    }

    public Mono<TaskDTO> create(TaskDTO taskDTO) {
        return repository.save(taskDTO.toEntity())
            .map(TaskDTO::fromEntityToDTO);
    }

    public Mono<Void> delete(Long id) {
        return repository.deleteById(id);
    }
}
