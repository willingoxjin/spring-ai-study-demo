package org.willingoxjin.springai.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.willingoxjin.springai.consts.PromptConst;
import org.willingoxjin.springai.entity.ChatMessageResponse;
import org.willingoxjin.springai.entity.ChatMessageSseRequest;
import org.willingoxjin.springai.search.SearchResult;
import org.willingoxjin.springai.service.ChatService;
import org.willingoxjin.springai.sse.SseEventType;
import org.willingoxjin.springai.sse.SseServer;
import reactor.core.publisher.Flux;

/**
 * Singleton Service
 * @author Jin.Nie
 */
@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    @Resource
    private ChatClient chatClient;

    @Resource
    private SseServer sseServer;

    @Override
    public String chatTest(String prompt) {
        String content = chatClient.prompt(prompt).call().content();
        log.debug("content: {}", content);
        return content;
    }

    @Override
    public Flux<String> chatStreamResponse(String prompt) {
        return chatClient.prompt(prompt).stream().content();
        // return chatClient.prompt()
        //         .user(prompt)
        //         .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
        //         .call()
        //         .content();
    }

    @Override
    public Flux<String> chatStreamResponse(String promptText, List<Document> ragDocContext) {
        Prompt prompt;
        if (CollectionUtils.isNotEmpty(ragDocContext)) {
            // RAG Prompt
            String ragContext = ragDocContext.stream().map(Document::getText).collect(Collectors.joining("\n"));
            String docPromptText = PromptConst.buildRagPrompt(ragContext, promptText);
            prompt = new Prompt(docPromptText);
        } else {
            // Default Prompt
            prompt = new Prompt(promptText);
        }

        return chatClient.prompt(prompt).stream().content();
    }

    @Override
    public Flux<String> chatStreamResponseFromSearch(String promptText, List<SearchResult> results) {
        Prompt prompt;
        if (CollectionUtils.isNotEmpty(results)) {
            String searchPrompt = results.stream()
                    .map(r -> String.format("""
                            <context>
                                [标题] %s
                                [来源] %s
                                [摘要] %s
                            </context>
                            """, r.getTitle(), r.getUrl(), r.getContent()))
                    .collect(Collectors.joining("\n"));
            String docPromptText = PromptConst.buildWebSearchPrompt(searchPrompt, promptText);
            prompt = new Prompt(docPromptText);
        } else {
            // Default Prompt
            prompt = new Prompt(promptText);
        }

        return chatClient.prompt(prompt).stream().content();
    }

    @Override
    public SseEmitter doChatOnStreamResponse(ChatMessageSseRequest request) {
        String clientId = request.getClientId();
        String sessionId = request.getSessionId();
        if (sessionId == null) {
            sessionId = UUID.fastUUID().toString();
        }
        SseEmitter sseEmitter = sseServer.connect(clientId);

        String finalSessionId = sessionId;
        new Thread(() -> {
            Flux<String> stringFlux = chatClient.prompt(request.getPrompt())
                    .stream()
                    .content();

            List<String> contentList = stringFlux.toStream()
                    .peek(content -> sseServer.sendMessage(SseEventType.CHUNK, clientId, content))
                    .collect(Collectors.toList());

            String fullContent = String.join("", contentList);
            ChatMessageResponse response = new ChatMessageResponse();
            response.setSessionId(finalSessionId);
            response.setContent(fullContent);
            sseServer.sendMessage(SseEventType.CLOSE, clientId, JSONUtil.toJsonStr(response));
        }).start();

        return sseEmitter;
    }

}
