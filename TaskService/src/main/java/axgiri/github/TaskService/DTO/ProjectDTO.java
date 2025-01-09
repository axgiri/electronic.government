package axgiri.github.TaskService.DTO;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import axgiri.github.TaskService.Model.Project;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDTO {

    private Long id;

    @NotNull(message = "name is required")
    private String name;

    @NotNull(message = "description is required")
    private String description;

    @NotNull(message = "company id is required")
    private Long companyId;

    private List<Long> usersId;

    public Project toEntity(ObjectMapper objectMapper) {
        Project project = new Project();
        project.setId(id);
        project.setName(name);
        project.setDescription(description);
        project.setCompanyId(companyId);

        try {
            if (usersId != null) {
                project.setUsersId(objectMapper.writeValueAsString(usersId));
            }
        } catch (Exception e) {
            throw new RuntimeException("failed to convert usersId to JSON", e);
        }

        return project;
    }

    public static ProjectDTO fromEntityToDTO(Project project, ObjectMapper objectMapper) {
        try {
            List<Long> usersId = project.getUsersId() != null
                ? objectMapper.readValue(project.getUsersId(), new TypeReference<List<Long>>() {})
                : null;
    
            return new ProjectDTO(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getCompanyId(),
                usersId
            );
        } catch (Exception e) {
            throw new RuntimeException("failed to convert usersId from JSON", e);
        }
    }
    
}

