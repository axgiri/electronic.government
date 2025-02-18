package axgiri.github.TaskService.handler.HandlerImpl;

import org.springframework.stereotype.Service;

import axgiri.github.TaskService.handler.HandlerService.TaskHandlerService;
import axgiri.github.TaskService.repository.TaskRepository;

@Service
public class TaskHandlerImpl implements TaskHandlerService {

    private final TaskRepository taskRepository;

    public TaskHandlerImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void deleteTasksByProjectId(Long projectId) {
        taskRepository.findByProjectId(projectId)
            .flatMap(task -> taskRepository.deleteById(task.getId()))
            .subscribe();
    }
}

