package org.willingoxjin.springai.consts;

/**
 *
 * @author Jin.Nie
 */
public class PromptConst {

    public static final String DEFAULT_SYSTEM_PROMPT = "你是一个人工智能助手，帮助用户回答问题，你的名字叫球球。";

    public static final String RAG_PROMPT = """
            基于上下文的知识库内容回答问题：
            【上下文】
            {context}
            
            【问题】
            {question}
            
            【输出】
            如果没有查到，请返回“您正在使用知识库查询，知识库中暂无此问题答案。”。
            如果查到，请返回具体的内容，不相关的近似内容不必输出。
            """;

    public static final String WEB_SEARCH_PROMPT = """
            你是一个搜索引擎，帮助用户搜索问题，请基于以下互联网返回的结果作为上下文，结合用户的提问，根据你的理解生成并输出结果：
            【上下文】
            {context}
            
            【问题】
            {question}
            
            【输出】
            如果查到，请根据结合搜索结果返回具体的内容。
            如果没有查到，只根据你的理解返回具体内容即可。
            """;

    public static String buildRagPrompt(String context, String question) {
        return RAG_PROMPT.replace("{context}", context).replace("{question}", question);
    }

    public static String buildWebSearchPrompt(String context, String question) {
        return WEB_SEARCH_PROMPT.replace("{context}", context).replace("{question}", question);
    }

}
