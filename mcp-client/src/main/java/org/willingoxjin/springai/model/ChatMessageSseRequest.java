package org.willingoxjin.springai.model;

import java.io.Serial;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Jin.Nie
 */
@Getter
@Setter
@ToString
public class ChatMessageSseRequest extends ChatMessageRequest {

    @Serial
    private static final long serialVersionUID = 1L;

    private String clientId;

    private String sessionId;

}
