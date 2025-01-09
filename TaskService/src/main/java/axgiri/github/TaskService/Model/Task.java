package axgiri.github.TaskService.Model;

import axgiri.github.TaskService.Enum.StatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

    @Column(nullable = false)
    private StatusEnum status;

    @Column(name = "user_id", nullable = false)
    private Long userId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;
    
    @Column(name = "created_at", nullable = false)
    private String createdAt;
    
    @Column(nullable = false)
    private String description;
}
