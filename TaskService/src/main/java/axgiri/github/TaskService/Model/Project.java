package axgiri.github.TaskService.Model;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "projects")
public class Project {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "users_id")
    private String usersId;

    public List<Long> getUsersIdAsList(ObjectMapper objectMapper) throws Exception {
        return objectMapper.readValue(usersId, new TypeReference<>() {});
    }

    public void setUsersIdAsList(List<Long> userIds, ObjectMapper objectMapper) throws Exception {
        this.usersId = objectMapper.writeValueAsString(userIds);
    }
}
