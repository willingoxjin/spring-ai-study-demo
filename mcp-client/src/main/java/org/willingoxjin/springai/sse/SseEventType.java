package org.willingoxjin.springai.sse;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author Jin.Nie
 */
@Getter
@AllArgsConstructor
public enum SseEventType {
    // 普通消息推送
    MESSAGE("message"),
    // 流式消息推送
    CHUNK("chunk"),
    // 关闭
    CLOSE("close"),
    ;

    private final String value;

}
