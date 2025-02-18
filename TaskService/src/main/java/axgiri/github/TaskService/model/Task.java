package axgiri.github.TaskService.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import axgiri.github.TaskService.Enum.StatusEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tasks")
public class Task {
    
    @Id
    private Long id;

    @Column("status")
    private StatusEnum status;

    @Column("user_id")
    private Long userId;

    @Column("project_id")
    private Long projectId;

    @Column("created_by")
    private Long createdBy;
    
    @Column("created_at")
    private String createdAt;
    
    @Column("description")
    private String description;
}
