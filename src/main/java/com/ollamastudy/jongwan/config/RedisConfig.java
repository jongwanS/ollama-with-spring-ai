package com.ollamastudy.jongwan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPooled;

@Configuration
public class RedisConfig {

    @Bean
    public JedisPooled jedisPooled() {
        // 예시: Redis가 localhost에서 6379 포트로 실행 중일 경우
        return new JedisPooled("localhost", 6379);
    }
}