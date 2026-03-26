package org.willingoxjin.springai.model;

import java.io.Serial;
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
public class ChatMessageContent implements java.io.Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String content;

    public static final class Builder {

        private ChatMessageContent chatMessageContent;

        public Builder() {
            chatMessageContent = new ChatMessageContent();
        }

        public Builder(ChatMessageContent other) {
            this.chatMessageContent = other;
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder content(String content) {
            chatMessageContent.setContent(content);
            return this;
        }

        public ChatMessageContent build() {
            return chatMessageContent;
        }
    }
}
