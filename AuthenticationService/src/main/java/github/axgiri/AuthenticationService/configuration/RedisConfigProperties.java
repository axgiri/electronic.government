package github.axgiri.AuthenticationService.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "spring.redis")
public class RedisConfigProperties {

    private String host;
    private int port;
    private long timeout = 5000;
    private long cacheTtl = 32400000;
}
