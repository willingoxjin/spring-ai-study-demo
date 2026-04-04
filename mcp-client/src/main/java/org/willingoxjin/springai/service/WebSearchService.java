package org.willingoxjin.springai.service;

import org.willingoxjin.springai.search.SearchResponse;

/**
 *
 * @author Jin.Nie
 */
public interface WebSearchService {

    /**
     * 实时搜索处理
     *
     * @param query 查询内容
     * @return 搜索结果
     */
    SearchResponse search(String query);

    <T> T search(String query, Class<T> responseType);
}
