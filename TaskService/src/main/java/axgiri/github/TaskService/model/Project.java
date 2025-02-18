package axgiri.github.TaskService.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "projects")
public class Project {
    
    @Id
    private Long id;

    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @Column("company_id")
    private Long companyId;

    @Column("users_id")
    private String usersId;

    public List<Long> getUsersIdAsList(ObjectMapper objectMapper) throws Exception {
        return objectMapper.readValue(usersId, new TypeReference<>() {});
    }

    public void setUsersIdAsList(List<Long> userIds, ObjectMapper objectMapper) throws Exception {
        this.usersId = objectMapper.writeValueAsString(userIds);
    }
}
