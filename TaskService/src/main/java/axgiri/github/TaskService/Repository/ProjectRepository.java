package axgiri.github.TaskService.Repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import axgiri.github.TaskService.Model.Project;
import reactor.core.publisher.Flux;

@Repository
public interface ProjectRepository extends ReactiveCrudRepository<Project, Long> {
    
    Flux<Project> findByCompanyId(Long companyId);
}
