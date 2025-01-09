package axgiri.github.TaskService.Service;

import org.springframework.stereotype.Service;

import axgiri.github.TaskService.DTO.TaskDTO;
import axgiri.github.TaskService.Repository.TaskRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Flux<TaskDTO> getAllTasks() {
        return taskRepository.findAll()
            .map(TaskDTO::fromEntityToDTO);
    }

    public Mono<TaskDTO> getTaskById(Long id) {
        return taskRepository.findById(id)
            .map(TaskDTO::fromEntityToDTO);
    }

    public Mono<TaskDTO> createTask(TaskDTO taskDTO) {
        return taskRepository.save(taskDTO.toEntity())
            .map(TaskDTO::fromEntityToDTO);
    }

    public Mono<Void> deleteTask(Long id) {
        return taskRepository.deleteById(id);
    }
}
