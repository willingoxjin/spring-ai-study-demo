package org.willingoxjin.springai.controller;

import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.willingoxjin.springai.model.ChatMessageContent;
import org.willingoxjin.springai.model.ChatMessageRequest;
import org.willingoxjin.springai.model.ChatMessageSseRequest;
import org.willingoxjin.springai.search.SearchResponse;
import org.willingoxjin.springai.search.SearchResult;
import org.willingoxjin.springai.service.ChatService;
import org.willingoxjin.springai.service.DocumentService;
import org.willingoxjin.springai.service.WebSearchService;
import org.willingoxjin.springai.sse.SseEventType;
import reactor.core.publisher.Flux;

/**
 *
 * @author Jin.Nie
 */
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Resource
    private ChatService chatService;

    @Resource
    private DocumentService documentService;

    @Resource
    private WebSearchService webSearchService;

    @GetMapping("/test")
    public String chatTest(String prompt) {
        return chatService.chatTest(prompt);
    }

    @GetMapping("/stream/response")
    public Flux<String> chatStreamResponse(@RequestParam String prompt) {
        return chatService.chatStreamResponse(prompt);
    }

    @GetMapping(value = "/stream/doChat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<ChatMessageContent>> chatOnStreamResponse(ChatMessageRequest request) {
        Flux<String> responseFlux = chatService.chatStreamResponse(request.getPrompt());
        return formatSse(responseFlux);
    }

    protected Flux<ServerSentEvent<ChatMessageContent>> formatSse(Flux<String> responseFlux) {
        return responseFlux.map(content -> ServerSentEvent.builder(ChatMessageContent.Builder.builder().content(content).build())
                .event(SseEventType.CHUNK.getValue())
                .build()
        ).concatWith(Flux.just(ServerSentEvent.builder(ChatMessageContent.Builder.builder().content("").build())
                .event(SseEventType.CLOSE.getValue())
                .build()
        ));
    }

    /**
     * 使用 application/json 格式传参
     * 注意：需要前端自定义处理请求，无法使用原生 js 的 EventSource
     */
    @PostMapping(value = "/stream/doChat")
    public Flux<ServerSentEvent<ChatMessageContent>> chatOnStreamResponsePost(@RequestBody ChatMessageRequest request) {
        return this.chatOnStreamResponse(request);
    }

    /**
     * 自定义 SSE 的方式
     */
    @GetMapping("/stream/doChatBySSE")
    public SseEmitter doChatOnStreamResponse(ChatMessageSseRequest request) {
        return chatService.doChatOnStreamResponse(request);
    }

    /**
     * 自定义 RAG 知识库
     */
    @GetMapping(value = "/stream/doChatByRag", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<ChatMessageContent>> chatByRag(ChatMessageRequest request) {
        String prompt = request.getPrompt();

        List<Document> ragDocContext = documentService.searchDocument(prompt);

        Flux<String> responseFlux = chatService.chatStreamResponse(prompt, ragDocContext);

        return formatSse(responseFlux);
    }

    @GetMapping(value = "/stream/doChatByWebSearch", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<ChatMessageContent>> chatByWebSearch(ChatMessageRequest request) {
        String prompt = request.getPrompt();
        SearchResponse searchResponse = webSearchService.search(prompt);
        Flux<String> responseFlux = chatService.chatStreamResponseFromSearch(prompt, searchResponse.getResults());

        return formatSse(responseFlux);
    }

}
