package com.wfh.aiagent.agent;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.wfh.aiagent.agent.model.AgentState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author FengHuan Wang
 * @Date 2025/5/29 9:22
 * @Version 1.0
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public class ToolCallAgent extends ReActAgent{
    // This class is used to represent a tool agent

    private final ToolCallback[] availableTools;

    private ChatResponse toolCallChatResponse;

    private final ToolCallingManager toolCallingManager;

    private final ChatOptions chatOptions;

    public ToolCallAgent(ToolCallback[] availableTools) {
        super();
        this.availableTools = availableTools;
        this.toolCallingManager = ToolCallingManager.builder()
                .build();
        this.chatOptions = DashScopeChatOptions.builder()
                .withProxyToolCalls(true)
                .build();
    }

    @Override
   public boolean think(){
        try {
            // 校验提示词，拼接用户提示词
            if (StrUtil.isNotBlank(getNextStepPrompt())){
                UserMessage userMessage = new UserMessage(getNextStepPrompt());
                getMessageList().add(userMessage);
            }
            // 调用AI,获取哦工具调用结果
            List<Message> messageList = getMessageList();
            Prompt prompt = new Prompt(messageList, this.chatOptions);
            ChatResponse chatResponse = getChatClient().prompt(prompt)
                    .system(getSystemPrompt())
                    .tools(availableTools)
                    .call()
                    .chatResponse();
            // 记录响应
            this.toolCallChatResponse = chatResponse;
            // 解析工具调用结果，获取要调用的工具
            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
            List<AssistantMessage.ToolCall> toolCallList = assistantMessage.getToolCalls();
            // 输出提示信息
            String result = assistantMessage.getText();
            log.info("AI调用结果：{}",result);
            log.info("AI调用工具：{}",toolCallList);
            String collect = toolCallList.stream()
                    .map(toolCall -> {
                        return String.format("调用工具：%s，参数：%s", toolCall.name(), toolCall.arguments());
                    }).collect(Collectors.joining("\n"));
            log.info("-----------------");
            log.info(collect);
            // 异常处理
            if (toolCallList.isEmpty()){
                getMessageList().add(assistantMessage);
                return false;
            }else {
                return true;
            }
        } catch (Exception e) {
            log.error("调用工具失败：",e);
            getMessageList().add(new AssistantMessage("出现错误" + e.getMessage()));
            return false;
        }
   }

   @Override
   public String act(){
        if (!toolCallChatResponse.hasToolCalls()){
            return "没有工具可以调用";
        }
       Prompt prompt = new Prompt(getMessageList(), this.chatOptions);
        // 调用工具
       ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, toolCallChatResponse);
       setMessageList(toolExecutionResult.conversationHistory());
       ToolResponseMessage toolResponseMessage = (ToolResponseMessage) CollectionUtil.getLast(toolExecutionResult.conversationHistory());
       // 判断是否调用了终止工具
       boolean terminate = toolResponseMessage.getResponses().stream()
               .anyMatch(response -> {
                   return response.name().equals("doTerminate");
               });
       if (terminate){
           setState(AgentState.FINISHED);
       }
       String collect = toolResponseMessage.getResponses()
               .stream()
               .map(toolResponse -> {
                   return "工具" + toolResponse.name() + "返回的结果" + toolResponse.responseData();
               }).collect(Collectors.joining("\n"));
       log.info(collect);
       return collect;
   }
}
