package axgiri.github.TaskService.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import axgiri.github.TaskService.model.Project;
import lombok.Data;

@Data
public class ProjectResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("company_id")
    private Long companyId;

    @JsonProperty("users_id")
    private List<Long> usersId;

    public static ProjectResponse fromEntityToDTO(Project project, ObjectMapper objectMapper) {
        try {
            List<Long> usersId = project.getUsersId() != null
                ? objectMapper.readValue(project.getUsersId(), new TypeReference<List<Long>>() {})
                : null;
            
            ProjectResponse response = new ProjectResponse();
            response.setId(project.getId());
            response.setName(project.getName());
            response.setDescription(project.getDescription());
            response.setCompanyId(project.getCompanyId());
            response.setUsersId(usersId);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("failed to convert usersId from JSON", e);
        }
    }
}
