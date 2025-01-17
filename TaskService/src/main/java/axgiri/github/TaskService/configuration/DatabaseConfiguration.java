package axgiri.github.TaskService.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;

@Configuration
public class DatabaseConfiguration extends AbstractR2dbcConfiguration {

    @Override
    @Bean
    public ConnectionFactory connectionFactory() {
        
        return new PostgresqlConnectionFactory(PostgresqlConnectionConfiguration.builder()
            .host("task_db")
            .port(5432)
            .username("postgres")
            .password("1")
            .database("taskdb")
            .build());
    }
}
