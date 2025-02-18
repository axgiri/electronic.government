package axgiri.github.TaskService.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import axgiri.github.TaskService.model.Project;
import reactor.core.publisher.Flux;

@Repository
public interface ProjectRepository extends ReactiveCrudRepository<Project, Long> {
    
    Flux<Project> findByCompanyId(Long companyId);
}
