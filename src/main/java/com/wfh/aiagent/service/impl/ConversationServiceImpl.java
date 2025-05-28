package com.wfh.aiagent.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wfh.aiagent.model.Conversation;
import com.wfh.aiagent.service.ConversationService;
import com.wfh.aiagent.mapper.ConversationMapper;
import org.springframework.stereotype.Service;

/**
* @author lenovo
* @description 针对表【conversation(历史消息对话表)】的数据库操作Service实现
* @createDate 2025-05-28 14:15:12
*/
@Service
public class ConversationServiceImpl extends ServiceImpl<ConversationMapper, Conversation>
    implements ConversationService{

}




