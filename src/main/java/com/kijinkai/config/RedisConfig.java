package com.kijinkai.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer; // JSON 직렬화를 위해 추가

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key 직렬화: String (가장 일반적)
        template.setKeySerializer(new StringRedisSerializer());
        // Value 직렬화: JSON (객체를 JSON 문자열로 저장)
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        // Hash Key 직렬화: String
        template.setHashKeySerializer(new StringRedisSerializer());
        // Hash Value 직렬화: JSON
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.afterPropertiesSet(); // 설정 적용
        return template;
    }
}
