package axgiri.github.TaskService.request;

import java.time.LocalDate;

import axgiri.github.TaskService.Enum.StatusEnum;
import axgiri.github.TaskService.model.Task;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskRequest {

    @NotNull(message = "status is required")
    private StatusEnum status;

    @NotNull(message = "userId is required")
    private Long userId;

    @NotNull(message = "projectId is required")
    private Long projectId;

    @NotNull(message = "createdBy is required")
    private Long createdBy;

    @NotNull(message = "description is required")
    private String description;

    public Task toEntity() {
        return new Task()
            .setStatus(status != null ? status : StatusEnum.TODO)
            .setUserId(userId)
            .setProjectId(projectId)
            .setCreatedBy(createdBy)
            .setCreatedAt(LocalDate.now().toString())
            .setDescription(description);
    }
}

