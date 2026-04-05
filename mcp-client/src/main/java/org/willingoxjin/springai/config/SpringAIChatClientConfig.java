package org.willingoxjin.springai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.Builder;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.willingoxjin.springai.consts.PromptConst;

/**
 *
 * @author Jin.Nie
 */
@Configuration
public class SpringAIChatClientConfig {

    @Bean
    public ChatClient defaultChatClient(ChatClient.Builder chatClientBuilder, ToolCallbackProvider toolCallbackProvider) {
        Builder builder = chatClientBuilder.defaultSystem(PromptConst.DEFAULT_SYSTEM_PROMPT)
                .defaultToolCallbacks(toolCallbackProvider);
        return builder.build();
    }

}
