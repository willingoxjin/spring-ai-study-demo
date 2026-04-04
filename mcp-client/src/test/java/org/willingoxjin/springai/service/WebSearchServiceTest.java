package org.willingoxjin.springai.service;

import static org.junit.jupiter.api.Assertions.*;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.willingoxjin.springai.search.SearchResponse;
import org.willingoxjin.springai.search.SearchResult;

/**
 *
 * @author Jin.Nie
 */
@Slf4j
@SpringBootTest
class WebSearchServiceTest {

    @Resource
    private WebSearchService webSearchService;

    @ParameterizedTest
    @ValueSource(strings = {
            // "spring boot",
            // "三角洲行动",
            "天霸Whys",
    })
    void testSearch(String query) {
        SearchResponse response = webSearchService.search(query);
        List<SearchResult> results = response.getResults();
        log.info("count: {}", results != null ? results.size() : 0);
        log.info("response: {}", response);
        assertNotNull(response);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            // "spring boot",
            "三角洲行动",
            // "天霸Whys",
    })
    void testSearchString(String query) {
        String response = webSearchService.search(query, String.class);
        log.info("response: {}", response);
        assertNotNull(response);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            // "spring boot",
            // "三角洲行动",
            "三角洲行动的红狼是谁？",
    })
    void testSearchString2(String query) {
        String s = HttpUtil.get("http://localhost:7788/search?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8) + "&format=json&engines=bing,baidu");
        String s1 = JSONUtil.formatJsonStr(s);
        log.info("response: {}", s1);
        log.info("response: {}", s);
    }

}