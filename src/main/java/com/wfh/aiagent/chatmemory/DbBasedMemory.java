package com.wfh.aiagent.chatmemory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.wfh.aiagent.model.Conversation;
import com.wfh.aiagent.service.ConversationService;
import jakarta.annotation.Resource;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * @Author FengHuan Wang
 * @Date 2025/5/25 13:24
 * @Version 1.0
 */
public class DbBasedMemory implements ChatMemory {


    @Resource
    private ConversationService conversationService;

    private static final Kryo kryo = new Kryo();

    static {
        kryo.setRegistrationRequired(false);
        // 设置实例化策略
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
    }

    public DbBasedMemory() {

    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        List<Message> messageList = getOrCreateConversation(conversationId);
        messageList.addAll(messages);
        saveConversation(conversationId, messageList);
    }

    @Override
    public void add(String conversationId, Message messages) {
       saveConversation(conversationId, List.of(messages));
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        List<Message> messageList = getOrCreateConversation(conversationId);
        return messageList.stream()
                .skip(Math.max(messageList.size() - lastN, 0)).toList();
    }

    @Override
    public void clear(String conversationId) {
        // 从数据库中删除对话
        Conversation conversation = conversationService.getById(conversationId);
        if (conversation != null) {
            conversationService.getBaseMapper().deleteById(conversation.getId());
        }
    }

    /**
     * 获取或创建会话消息的列表(从mysql数据库中读取)
     * @param conversationId
     * @return
     */
    private List<Message> getOrCreateConversation(String conversationId) {
        // 从mysql中创建或获取
        Conversation conversation = conversationService.getById(Integer.valueOf(conversationId));
        // 如果对话为空，就是创建会话消息
        String message = conversation.getMessage();
        byte[] messageBytes = message.getBytes();
        List<Message> messages = new ArrayList<>();
        if (conversation != null) {
            try {
                Input input = new Input(messageBytes);
                // 读取对象中的会话消息
                messages = kryo.readObject(input, ArrayList.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return messages;
    }

    /**
     * 保存会话信息
     * @param conversationId
     * @param messages
     */
    private void saveConversation(String conversationId, List<Message> messages) {
        // 创建对话消息对象
        Conversation conversation = new Conversation();
        Output output = new Output(1024, -1);
        kryo.writeObject(output, messages);
        output.close();
        byte[] bytes = output.toBytes();
        String conversationStr = Base64.getEncoder().encodeToString(bytes);
        conversation.setMessage(conversationStr);
        conversation.setId(Integer.valueOf(conversationId));
        try {
            conversationService.save(conversation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
