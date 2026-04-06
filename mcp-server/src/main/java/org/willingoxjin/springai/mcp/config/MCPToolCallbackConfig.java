package org.willingoxjin.springai.mcp.config;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.willingoxjin.springai.mcp.tool.DateTool;

/**
 * @author Jin.Nie
 */
@Configuration
public class MCPToolCallbackConfig {

    @Bean
    public ToolCallbackProvider toolCallbackProvider(DateTool dateTool) {
        return MethodToolCallbackProvider.builder()
                // 注册 MCP 工具
                .toolObjects(dateTool)
                .build();
    }

}
