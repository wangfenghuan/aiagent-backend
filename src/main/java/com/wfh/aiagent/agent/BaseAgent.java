package com.wfh.aiagent.agent;

import cn.hutool.core.util.StrUtil;
import com.wfh.aiagent.agent.model.AgentState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author FengHuan Wang
 * @Date 2025/5/29 9:21
 * @Version 1.0
 */
@Data
@Slf4j
public abstract class BaseAgent {

    /**
     * 核心属性
     */
    private String name;

    /**
     * 提示词
     */
    private String systemPrompt;
    private String nextStepPrompt;

    /**
     * 状态
     */
    private AgentState state = AgentState.IDLE;

    /**
     * 执行控制
     */
    private int maxSteps = 10;
    private int currentStep = 0;

    /**
     * LLM
     */
    private ChatClient chatClient;

    /**
     * Memory（需要自主维护会话上下文）
     */
    private List<Message> messageList = new ArrayList<>();

    /**
     * 运行代理
     * @param userPrompt
     * @return
     */
    public String run(String userPrompt){
        // 基础校验
        if (this.state != AgentState.IDLE){
            throw new RuntimeException("Can not agent feom state:" + this.state);
        }
        if (StrUtil.isBlank(userPrompt)){
            throw new RuntimeException("userPrompt can not be null");
        }
        // 执行，更改状态
        this.state = AgentState.RUNNING;
        // 记录消息上下文
        messageList.add(new UserMessage(userPrompt));
        // 保存结果列表
        List<String> results = new ArrayList<>();

        try{
            // 执行循环
            for (int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {
                int stepNumber = i + 1;
                currentStep = stepNumber;
                log.info("[{}] step {} start", stepNumber, maxSteps);
                // 单步执行
                String stepResult = step();
                String result = "Step" + stepNumber + " result:" + stepResult;
                results.add(result);
            }
            // 检查是否超出步骤限制
            if (currentStep >= maxSteps){
                state = AgentState.FINISHED;
                results.add("Terminated due to step limit:" + maxSteps);
            }
        }catch (Exception e){
            state = AgentState.ERROR;
            log.error("Agent run error", e);
        }finally {
            cleanup();
        }
        return String.join("\n", results);
    }

    /**
     * 执行单个步骤
     * @return
     */
    public abstract String step();

    /**
     * 清理资源
     */
    protected void cleanup(){

    }
}
