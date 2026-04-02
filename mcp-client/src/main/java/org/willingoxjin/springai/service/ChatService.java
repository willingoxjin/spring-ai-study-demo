package org.willingoxjin.springai.service;

import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.willingoxjin.springai.model.ChatMessageRequest;
import org.willingoxjin.springai.model.ChatMessageSseRequest;
import reactor.core.publisher.Flux;

/**
 *
 * @author Jin.Nie
 */
public interface ChatService {

    String chatTest(String prompt);

    Flux<String> chatStreamResponse(String prompt);

    Flux<String> chatStreamResponse(String prompt, List<Document> ragDocContext);

    SseEmitter doChatOnStreamResponse(ChatMessageSseRequest request);

}
