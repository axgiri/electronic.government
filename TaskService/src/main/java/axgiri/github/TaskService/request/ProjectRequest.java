package axgiri.github.TaskService.request;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import axgiri.github.TaskService.model.Project;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProjectRequest {

    @NotNull(message = "name is required")
    private String name;

    @NotNull(message = "description is required")
    private String description;

    @NotNull(message = "company id is required")
    private Long companyId;

    private List<Long> usersId;

    public Project toEntity(ObjectMapper objectMapper) {
        Project project = new Project();
        project.setName(name);
        project.setDescription(description);
        project.setCompanyId(companyId);

        try {
            if (usersId != null) {
                project.setUsersId(objectMapper.writeValueAsString(usersId));
            } else {
                project.setUsersId(null);
            }
        } catch (Exception e) {
            throw new RuntimeException("failed to convert usersId to JSON", e);
        }

        return project;
    }
}
