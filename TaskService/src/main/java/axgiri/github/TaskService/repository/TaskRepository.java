package axgiri.github.TaskService.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import axgiri.github.TaskService.model.Task;
import reactor.core.publisher.Flux;

@Repository
public interface TaskRepository extends ReactiveCrudRepository<Task, Long> {

    Flux<Task> findByProjectId(Long projectId);
    
    Flux<Task> findByUserId(Long userId);
}
