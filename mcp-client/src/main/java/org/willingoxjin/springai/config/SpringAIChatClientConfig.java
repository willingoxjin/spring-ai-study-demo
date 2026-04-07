package org.willingoxjin.springai.config;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.Builder;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
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

    @Resource
    private ChatMemory chatMemory;

    @Bean
    public ChatClient defaultChatClient(ChatClient.Builder chatClientBuilder, ToolCallbackProvider toolCallbackProvider) {
        // 记忆功能
        MessageChatMemoryAdvisor chatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();

        Builder builder = chatClientBuilder.defaultSystem(PromptConst.DEFAULT_SYSTEM_PROMPT)
                .defaultToolCallbacks(toolCallbackProvider)
                .defaultAdvisors(chatMemoryAdvisor);
        return builder.build();
    }

}
