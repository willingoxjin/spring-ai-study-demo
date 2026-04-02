package org.willingoxjin.springai.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.groovy.util.Maps;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

/**
 *
 * @author Jin.Nie
 */
@Slf4j
@SpringBootTest
class DocumentServiceTest {

    @jakarta.annotation.Resource
    private DocumentService documentService;

    @ParameterizedTest
    @CsvSource({
            "file:/Users/willingoxjin/Downloads/三一分化力量训练方案（通用版）.md",
    })
    void loadDocText(String resourceUrl) {
        Resource resource = (new DefaultResourceLoader()).getResource(resourceUrl);
        Map<String, Object> metadata = Maps.of("filename", resource.getFilename());
        List<Document> documents = documentService.loadDocText(resource, metadata);

        // 保存文档内容到向量库
        documentService.saveDocument(documents);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "file:/Users/willingoxjin/Downloads/三一分化力量训练方案（通用版）.pdf",
            "file:/Users/willingoxjin/Downloads/三一分化力量训练方案（通用版）.docx",
    })
    void loadDoc(String resourceUrl) {
        Resource resource = (new DefaultResourceLoader()).getResource(resourceUrl);
        TikaDocumentReader reader = new TikaDocumentReader(resource);
        List<Document> documents = reader.get();

        // 切分文档内容
        for (Document document : documents) {
            System.err.println(document);
            System.out.println();
        }
    }


    @ParameterizedTest
    @CsvSource({
            // "三一分化",
            "训练的关键注意事项是什么？"
    })
    void searchDocument(String query) {
        List<Document> documents = documentService.searchDocument(query);
        for (Document document : documents) {
            log.info("=====> document: \n{} \n", document);
        }
    }

}