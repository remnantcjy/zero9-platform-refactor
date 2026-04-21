package com.zero9platform.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisConfig {

    /**
     * Redis 연결 설정
     */
    @Value("${spring.data.redis.host:localhost}") // 값이 없으면 localhost 사용
    private String host;

    @Value("${spring.data.redis.port:6379}")
    private int port;

    /**
     * Redis 연결 설정
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // application.yml 에서 spring.redis.* 설정을 자동 사용
        return new LettuceConnectionFactory(host, port); // 비동기 커넥션
    }

    /**
     * 직접 코드에서 사용할 템플릿
     * Key/Value를 String 고정으로 쓰는 도구 (직렬화 직접 설정 X)
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }

    /**
     * @Cacheable 설정 (JSON 객체 저장용)
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  // LocalDateTime 지원
        objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);  // 클래스 타입 정보 저장 (역직렬화 시 필수)

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper)))
                .entryTtl(Duration.ofHours(1));

        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .build();
    }
}