package org.willingoxjin.springai.controller;

import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.willingoxjin.springai.sse.SseServer;

/**
 *
 * @author Jin.Nie
 */
@RestController
@RequestMapping("/sse")
public class SseTestController {

    @Resource
    private SseServer chatSseServer;

    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(String clientId) {
        return chatSseServer.connect(clientId);
    }

    @GetMapping("/sendMessage/test")
    public Boolean sendMessageTest(String clientId, String message) {
        return chatSseServer.sendMessage(clientId, message);
    }

}
