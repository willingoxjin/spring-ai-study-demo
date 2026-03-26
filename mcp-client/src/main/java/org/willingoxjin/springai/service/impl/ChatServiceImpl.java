package org.willingoxjin.springai.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.willingoxjin.springai.model.ChatMessageRequest;
import org.willingoxjin.springai.model.ChatMessageResponse;
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
    }

    @Override
    public SseEmitter doChatOnStreamResponse(ChatMessageRequest request) {
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
