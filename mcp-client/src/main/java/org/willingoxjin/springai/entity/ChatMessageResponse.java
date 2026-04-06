package org.willingoxjin.springai.entity;

import java.io.Serial;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Jin.Nie
 */
@Getter
@Setter
@ToString
public class ChatMessageResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String content;

    private String sessionId;

}
