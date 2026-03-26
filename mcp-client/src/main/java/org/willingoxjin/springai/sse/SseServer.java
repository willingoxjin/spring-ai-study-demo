package org.willingoxjin.springai.sse;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

/**
 *
 * @author Jin.Nie
 */
@Slf4j
@Service
public class SseServer {

    private static final long SSE_TIMEOUT = 30_60_1000L;

    private static final ConcurrentMap<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter connect(String clientId) {
        SseEmitter emitter;
        if (emitters.containsKey(clientId) && emitters.get(clientId) != null) {
            emitter = emitters.get(clientId);
            emitter.complete();
            emitters.remove(clientId);
        }

        // timeout=0L表示永不过期，默认30s，超时将抛出异常
        emitter = new SseEmitter(SSE_TIMEOUT);
        emitter.onTimeout(() -> onTimeout(clientId));
        emitter.onCompletion(() -> onCompletion(clientId));
        emitter.onError((throwable) -> onError(clientId, throwable));
        log.info("SseEmitter connect, clientId={}", clientId);
        emitters.put(clientId, emitter);

        heartbeat(emitter);

        return emitters.get(clientId);
    }

    private void heartbeat(SseEmitter emitter) {
        try {
            emitter.send(SseEmitter.event().comment("SSE_HEARTBEAT"));
        } catch (IOException e) {
            emitter.completeWithError(e);
            throw new RuntimeException(e);
        }
    }

    protected void onTimeout(String clientId) {
        log.error("SseEmitter timeout, clientId={}", clientId);
        emitters.remove(clientId);
    }

    protected void onCompletion(String clientId) {
        log.info("SseEmitter completion, clientId={}", clientId);
        emitters.remove(clientId);
    }

    protected void onError(String clientId, Throwable throwable) {
        log.error("SseEmitter error, clientId={}", clientId, throwable);
        emitters.remove(clientId);
    }

    public boolean sendMessage(String clientId, String message) {
        return sendMessage(SseEventType.MESSAGE, clientId, message);
    }

    public boolean sendMessage(SseEventType eventType, String clientId, String message) {
        if (CollectionUtils.isEmpty(emitters) || !emitters.containsKey(clientId)) {
            log.warn("SseEmitter sendMessage, emitters is empty or not contains clientId={}", clientId);
            return false;
        }

        SseEmitter emitter = emitters.get(clientId);
        if (emitter == null) {
            return false;
        }

        return sendMessage(emitter, eventType, clientId, message);
    }

    public boolean sendMessage(SseEmitter emitter, SseEventType eventType, String clientId, String message) {
        log.debug("SseEmitter sendMessage, eventType={}, clientId={}, message={}", eventType, clientId, message);
        doSendMessage(emitter, buildEvent(eventType, clientId, message));
        return true;
    }


    private SseEventBuilder buildEvent(SseEventType eventType, String clientId, String message) {
        return SseEmitter.event()
                .id(clientId)
                .name(eventType.getValue())
                .data(message);
    }

    protected void doSendMessage(SseEmitter emitter, SseEventBuilder eventBuilder) {
        try {
            emitter.send(eventBuilder.build());
        } catch (IOException e) {
            emitter.completeWithError(e);
            throw new RuntimeException(e);
        }
    }

}
