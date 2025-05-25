package com.wfh.aiagent.controller;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;

/**
 * @Author FengHuan Wang
 * @Date 2025/5/24 19:32
 * @Version 1.0
 */
public class LangChain4j {
    public static void main(String[] args) {
        ChatLanguageModel model = QwenChatModel.builder()
                .apiKey("sk-3e4b3b0a5513440f80a23349057c653f")
                .modelName("qwen-max")
                .build();
        String chat = model.chat("我是一名Java后端开发者，你好啊");
        System.out.println(chat);
    }
}
