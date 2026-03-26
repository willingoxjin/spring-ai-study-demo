package org.willingoxjin.springai.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.willingoxjin.springai.model.ChatMessageRequest;
import reactor.core.publisher.Flux;

/**
 *
 * @author Jin.Nie
 */
public interface ChatService {

    String chatTest(String prompt);

    Flux<String> chatStreamResponse(String prompt);

    SseEmitter doChatOnStreamResponse(ChatMessageRequest request);

}
