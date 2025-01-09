package axgiri.github.TaskService.Repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import axgiri.github.TaskService.Model.Project;
import reactor.core.publisher.Flux;

public interface ProjectRepository extends ReactiveCrudRepository<Project, Long> {
    Flux<Project> findByCompanyId(Long companyId);
}
