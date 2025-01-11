package axgiri.github.TaskService.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import axgiri.github.TaskService.Enum.StatusEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tasks")
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
