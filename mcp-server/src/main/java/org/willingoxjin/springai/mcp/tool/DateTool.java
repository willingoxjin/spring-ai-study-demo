package org.willingoxjin.springai.mcp.tool;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.willingoxjin.springai.mcp.entity.CurrentTimeRequest;

/**
 *
 * @author Jin.Nie
 */
@Slf4j
@Component
public class DateTool {

    private static final String DATETIME_RESULT = "当前时间是：%s";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Tool(name = "get_current_time", description = "获取当前时间")
    public String getCurrentTime() {
        log.info("===== Call MCP Tool: get_current_time =====");
        String currentDateTime = LocalDateTime.now().format(FORMATTER);
        return String.format(DATETIME_RESULT, currentDateTime);
    }

    @Tool(name = "get_current_time_by_zone", description = "根据城市所在时区id获取当前时间")
    public String getCurrentTimeByZone(@ToolParam(description = "城市") String cityName,
            @ToolParam(description = "时区id") String zoneId) {
        log.info("===== Call MCP Tool: get_current_time_by_zone =====");
        log.info("===== cityName: {}, zoneId: {} =====", cityName, zoneId);

        ZoneId zone = ZoneId.of(zoneId);
        ZonedDateTime dateTime = ZonedDateTime.now(zone);
        String currentDateTime = dateTime.format(FORMATTER);
        return String.format(DATETIME_RESULT, currentDateTime);
    }

    // @Tool(name = "get_current_time_by_zone", description = "根据城市所在时区id获取当前时间")
    public String getCurrentTimeByZone(CurrentTimeRequest request) {
        String cityName = request.cityName();
        String zoneId = request.zoneId();
        log.info("===== Call MCP Tool: get_current_time_by_zone =====");
        log.info("===== cityName: {}, zoneId: {} =====", cityName, zoneId);

        ZoneId zone = ZoneId.of(zoneId);
        ZonedDateTime dateTime = ZonedDateTime.now(zone);
        String currentDateTime = dateTime.format(FORMATTER);
        return String.format(DATETIME_RESULT, currentDateTime);
    }

}
