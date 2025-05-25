package com.wfh.aiagent.chatmemory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.wfh.aiagent.model.Conversation;
import com.wfh.aiagent.service.ConversationService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

/**
 * @Author FengHuan Wang
 * @Date 2025/5/25 13:24
 * @Version 1.0
 */
@Component
@Slf4j
public class DbBasedMemory implements ChatMemory {


    @Resource
    private ConversationService conversationService;

    private static final Kryo kryo = new Kryo();

    static {
        kryo.setRegistrationRequired(false);
        kryo.register(Conversation.class);
        kryo.register(ArrayList.class);
        kryo.register(MessageType.class);
        kryo.register(HashMap.class);
        kryo.register(org.springframework.ai.chat.messages.UserMessage.class);
        kryo.register(org.springframework.ai.chat.messages.AssistantMessage.class);
        kryo.register(org.springframework.ai.chat.messages.SystemMessage.class);
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
        try {
            Conversation conversation = conversationService.getById(conversationId);
            if (conversation != null && conversation.getMessage() != null) {
                byte[] bytes = Base64.getDecoder().decode(conversation.getMessage());
                try (Input input = new Input(bytes)) {
                    return kryo.readObject(input, ArrayList.class);
                }catch (Exception e){
                    log.error("反序列化失败：{}", e.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * 保存会话信息
     * @param conversationId
     * @param messages
     */
    private void saveConversation(String conversationId, List<Message> messages) {
        try (Output output = new Output(1024, -1)) {
            kryo.writeObject(output, messages);
            byte[] bytes = output.toBytes();

            Conversation conversation = new Conversation();
            conversation.setId(Integer.valueOf(conversationId));
            conversation.setMessage(Base64.getEncoder().encodeToString(bytes));

            if (conversationService.getById(conversationId) != null) {
                conversationService.updateById(conversation);
            } else {
                conversationService.save(conversation);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
