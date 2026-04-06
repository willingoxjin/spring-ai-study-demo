package org.willingoxjin.springai.mcp.entity;

import org.springframework.ai.tool.annotation.ToolParam;

/**
 *
 * @author Jin.Nie
 */
public record CurrentTimeRequest(
        @ToolParam(description = "城市名称")
        String cityName,

        @ToolParam(description = "时区id")
        String zoneId
) {}
