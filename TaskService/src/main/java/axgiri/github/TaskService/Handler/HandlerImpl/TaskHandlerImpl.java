package axgiri.github.TaskService.Handler.HandlerImpl;

import org.springframework.stereotype.Service;

import axgiri.github.TaskService.Handler.HandlerService.TaskHandlerService;
import axgiri.github.TaskService.Repository.TaskRepository;

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

