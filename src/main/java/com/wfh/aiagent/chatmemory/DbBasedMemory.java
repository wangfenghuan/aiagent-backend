package com.wfh.aiagent.chatmemory;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;

import java.util.List;

/**
 * @Author FengHuan Wang
 * @Date 2025/5/25 13:24
 * @Version 1.0
 */
public class DbBasedMemory implements ChatMemory {
    @Override
    public void add(String conversationId, List<Message> messages) {

    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        return null;
    }

    @Override
    public void clear(String conversationId) {

    }
}
