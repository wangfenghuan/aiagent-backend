package com.wfh.aiagent.tools;

import jakarta.annotation.Resource;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @Author FengHuan Wang
 * @Date 2025/5/28 9:17
 * @Version 1.0
 */
@Configuration
@Component
public class ToolRegisteration {

    @Resource
    private PDFGenerationTool pdfGenerationTool;
    @Bean
    public ToolCallback[] allTools(){
        WebSearchTool webSearchTool = new WebSearchTool();
        FileOperationTool fileOperationTool = new FileOperationTool();
        WebScrapingTool webScrapingTool = new WebScrapingTool();
        return ToolCallbacks.from(
                pdfGenerationTool,
                webSearchTool,
                fileOperationTool,
                webScrapingTool
        );
    }
}
