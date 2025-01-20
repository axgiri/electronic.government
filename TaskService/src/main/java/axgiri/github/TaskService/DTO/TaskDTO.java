package axgiri.github.TaskService.DTO;

import java.time.LocalDateTime;

import axgiri.github.TaskService.Enum.StatusEnum;
import axgiri.github.TaskService.Model.Task;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {

    private Long id;

    private StatusEnum status;

    @NotNull(message = "userId is required")
    private Long userId;

    @NotNull(message = "projectId is required")
    private Long projectId;

    @NotNull(message = "createdBy is required")
    private Long createdBy;

    private String createdAt;

    @NotNull(message = "description is required")
    private String description;

    public Task toEntity() {
        Task task = new Task();
        task.setId(id);
        task.setStatus(status != null ? status : StatusEnum.TODO);
        task.setUserId(userId);
        task.setProjectId(projectId);
        task.setCreatedBy(createdBy);
        task.setCreatedAt(LocalDateTime.now().toString());
        task.setDescription(description);
        return task;
    }

    public static TaskDTO fromEntityToDTO(Task task) {
        return new TaskDTO(
            task.getId(),
            task.getStatus(),
            task.getUserId(),
            task.getProjectId(),
            task.getCreatedBy(),
            task.getCreatedAt(),
            task.getDescription()
        );
    }
}

