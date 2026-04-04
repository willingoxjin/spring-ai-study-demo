package org.willingoxjin.springai.service.impl;

import jakarta.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.willingoxjin.springai.search.SearchResponse;
import org.willingoxjin.springai.search.SearchResult;
import org.willingoxjin.springai.service.WebSearchService;

/**
 *
 * @author Jin.Nie
 */
@Slf4j
@Service
public class SearXNGWebSearchServiceImpl implements WebSearchService {

    private static final String SEARCH_URL = "http://localhost:7788/search?q={query}&format=json&engines=bing,baidu";

    /** 搜索返回的结果集最多取多少 */
    private static final Integer SEARCH_LIMIT = 25;

    @Resource
    private RestClient restClient;

    @Override
    public SearchResponse search(String query) {
        SearchResponse response = search(query, SearchResponse.class);
        log.debug("search response: {}", response);
        SearchResponse limitResp = limitSearchResults(response);
        return limitResp;
    }

    @Override
    public <T> T search(String query, Class<T> responseType) {
        return restClient.get()
                .uri(SEARCH_URL, query)
                .retrieve()
                .body(responseType);
    }

    protected static SearchResponse limitSearchResults(SearchResponse response) {
        if (response == null) {
            return null;
        }
        List<SearchResult> results = response.getResults();
        if (results == null || results.isEmpty()) {
            return response;
        }

        List<SearchResult> limitResults = results.subList(0, Math.min(results.size(), SEARCH_LIMIT))
                .parallelStream()
                .sorted(Comparator.comparingDouble(SearchResult::getScore)
                .reversed())
                .limit(SEARCH_LIMIT)
                .toList();

        response.setResults(limitResults);
        return response;
    }

}
