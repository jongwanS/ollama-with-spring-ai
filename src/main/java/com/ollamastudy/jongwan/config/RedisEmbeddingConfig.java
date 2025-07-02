package com.ollamastudy.jongwan.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisPooled;

@Configuration
public class RedisEmbeddingConfig {
    @Bean
    public RedisVectorStore vectorStore(EmbeddingModel embeddingModel, JedisPooled jedis) {
        return RedisVectorStore.builder(jedis, embeddingModel)
                .build();
    }
}