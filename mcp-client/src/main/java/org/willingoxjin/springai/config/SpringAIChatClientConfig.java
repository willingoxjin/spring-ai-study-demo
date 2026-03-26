package org.willingoxjin.springai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.Builder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Jin.Nie
 */
@Configuration
public class SpringAIChatClientConfig {

    private static final String DEFAULT_SYSTEM_PROMPT = "你是一个人工智能助手，帮助用户回答问题，你的名字叫球球。";

    @Bean
    public ChatClient defaultChatClient(ChatClient.Builder chatClientBuilder) {
        Builder builder = chatClientBuilder.defaultSystem(DEFAULT_SYSTEM_PROMPT);
        return builder.build();
    }

}
