package com.wfh.aiagent.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @Author FengHuan Wang
 * @Date 2025/5/24 19:23
 * @Version 1.0
 */
@Component
public class SpringAiInvoke implements CommandLineRunner {

    @Resource
    private ChatModel dashscopeModel;


    @Override
    public void run(String... args) throws Exception {
        AssistantMessage output = dashscopeModel.call(new Prompt("你好，我是一名Java全栈开发者")).getResult()
                .getOutput();
        System.out.println(output.getText());
    }
}
