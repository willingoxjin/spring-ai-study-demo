package org.willingoxjin.springai.service.impl;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.willingoxjin.springai.service.DocumentService;
import redis.clients.jedis.JedisPooled;

/**
 *
 * @author Jin.Nie
 */
@Slf4j
@Service
public class DocumentServiceImpl implements DocumentService {

    @jakarta.annotation.Resource
    private RedisVectorStore redisVectorStore;

    @Override
    public List<Document> loadDocText(Resource resource, Map<String, Object> metadata) {
        // 读取文件内容
        TextReader reader = new TextReader(resource);
        Map<String, Object> customMetadata = reader.getCustomMetadata();
        if (MapUtils.isNotEmpty(metadata)) {
            customMetadata.putAll(metadata);
        }
        List<Document> documents = reader.get();

        // 切分文档内容
        TextSplitter textSplitter = new CustomTokenTextSplitter();
        List<Document> splitDocs = textSplitter.apply(documents);

        if (log.isDebugEnabled()) {
            for (Document splitDoc : splitDocs) {
                log.debug("splitDoc: {} \n", splitDoc);
            }
        }

        return splitDocs;
    }

    @Override
    public void saveDocument(List<Document> documents) {
        // 添加到向量存储中
        redisVectorStore.add(documents);
    }

    @Override
    public List<Document> searchDocument(String query) {
        return redisVectorStore.similaritySearch(query);
    }

    static class CustomTokenTextSplitter extends TextSplitter {

        // 使用 markdown 标题切分
        private static final Pattern CHAPTER_PATTERN = Pattern.compile("(?=^(#|##) )", Pattern.MULTILINE);

        @Override
        protected List<String> splitText(String text) {
            return List.of(CHAPTER_PATTERN.split(text.trim()));
        }

    }

}
