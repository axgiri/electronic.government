package github.axgiri.AuthenticationService.Configuration;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
@EnableCaching
@EnableConfigurationProperties(RedisConfigProperties.class)
public class RedisConfiguration {

    private final RedisConfigProperties properties;

    public RedisConfiguration(RedisConfigProperties properties) {
        this.properties = properties;
    }

    @Bean
    public ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(properties.getHost(), properties.getPort());
        lettuceConnectionFactory.setValidateConnection(true);
        return lettuceConnectionFactory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory, ObjectMapper redisObjectMapper) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        RedisSerializer<String> keySerializer = new StringRedisSerializer();
        RedisSerializer<Object> valueSerializer = new GenericJackson2JsonRedisSerializer(redisObjectMapper);

        template.setKeySerializer(keySerializer);
        template.setValueSerializer(valueSerializer);

        template.setHashKeySerializer(keySerializer);
        template.setHashValueSerializer(valueSerializer);

        return template;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory, ObjectMapper redisObjectMapper) {
        RedisSerializer<String> keySerializer = new StringRedisSerializer();
        RedisSerializer<Object> valueSerializer = new GenericJackson2JsonRedisSerializer(redisObjectMapper);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMillis(properties.getCacheTtl()))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer));

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        RedisCacheConfiguration oneHourCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer));

        RedisCacheConfiguration fourHoursCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(4))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer));

        cacheConfigurations.put("oneHourCache", oneHourCacheConfig);
        cacheConfigurations.put("fourHoursCache", fourHoursCacheConfig);

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
