package axgiri.github.TaskService.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import axgiri.github.TaskService.Enum.StatusEnum;
import axgiri.github.TaskService.model.Task;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TaskResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("status")
    private StatusEnum status;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("project_id")
    private Long projectId;

    @JsonProperty("created_by")
    private Long createdBy;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("description")
    private String description;

    public static TaskResponse fromEntityToDTO(Task task) {
        return new TaskResponse()
            .setId(task.getId())
            .setStatus(task.getStatus())
            .setUserId(task.getUserId())
            .setProjectId(task.getProjectId())
            .setCreatedBy(task.getCreatedBy())
            .setCreatedAt(task.getCreatedAt())
            .setDescription(task.getDescription());
    }
}

