package org.willingoxjin.springai.vector;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * RedisVectorStore 工厂
 * @author Jin.Nie
 */
@Service
public class RedisVectorStoreFactory {

    private static final String DEFAULT_PREFIX = "RAG_DOC";

    private static final String SEPARATOR = ":";

    private final RedisVectorStore redisVectorStore;

    private final EmbeddingModel embeddingModel;

    @Value("${spring.ai.vectorstore.redis.prefix}")
    private String commonPrefix;

    public RedisVectorStoreFactory(RedisVectorStore redisVectorStore, EmbeddingModel embeddingModel) {
        this.redisVectorStore = redisVectorStore;
        this.embeddingModel = embeddingModel;
    }

    /**
     * 根据前缀获取 RedisVectorStore
     *
     * @param prefix 前缀
     * @return RedisVectorStore
     */
    public RedisVectorStore getRedisVectorStoreByPrefix(String prefix) {
        String defaultPrefix = DEFAULT_PREFIX;
        if (this.commonPrefix != null) {
            defaultPrefix = this.commonPrefix;
        }

        return RedisVectorStore.builder(redisVectorStore.getJedis(), embeddingModel)
                .prefix(defaultPrefix + SEPARATOR + prefix + SEPARATOR)
                .build();
    }

}
