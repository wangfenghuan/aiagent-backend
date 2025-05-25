package com.wfh.aiagent.mapper;

import com.wfh.aiagent.model.Conversation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;

/**
* @author lenovo
* @description 针对表【conversation(历史消息对话表)】的数据库操作Mapper
* @createDate 2025-05-25 13:35:32
* @Entity generator.domain.Conversation
*/
@Mapper
public interface ConversationMapper extends BaseMapper<Conversation> {

}




