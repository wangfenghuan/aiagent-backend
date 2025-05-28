package com.wfh.aiagent.tools;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.IOException;

/**
 * @Author FengHuan Wang
 * @Date 2025/5/27 20:43
 * @Version 1.0
 */
public class WebScrapingTool {

    @Tool(description = "Scrape information from a web page")
    public String scrapeWebPage(@ToolParam(description = "URL of the web page to scrape") String url){
        Document document;
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return document.html();
    }

}
