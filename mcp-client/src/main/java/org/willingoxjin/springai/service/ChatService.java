package org.willingoxjin.springai.service;

import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.willingoxjin.springai.entity.ChatMessageSseRequest;
import org.willingoxjin.springai.search.SearchResult;
import reactor.core.publisher.Flux;

/**
 *
 * @author Jin.Nie
 */
public interface ChatService {

    String chatTest(String prompt);

    Flux<String> chatStreamResponse(String prompt);

    Flux<String> chatStreamResponse(String prompt, List<Document> ragDocContext);

    Flux<String> chatStreamResponseFromSearch(String prompt, List<SearchResult> results);

    SseEmitter doChatOnStreamResponse(ChatMessageSseRequest request);

}
