package org.willingoxjin.springai.service;

import java.util.List;
import java.util.Map;
import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;

/**
 * @author Jin.Nie
 */
public interface DocumentService {

    /**
     * 加载文档内容
     *
     * @param resource 文档资源
     * @param metadata 文件名
     */
    List<Document> loadDocText(Resource resource, Map<String, Object> metadata);

    /**
     * 保存文档内容
     *
     * @param documents 文档内容
     */
    void saveDocument(List<Document> documents);

    /**
     * 搜索文档内容
     *
     * @param query 查询内容
     */
    List<Document> searchDocument(String query);

}
