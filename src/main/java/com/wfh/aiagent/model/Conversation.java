package com.wfh.aiagent.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 历史消息对话表
 * @TableName conversation
 */
@TableName(value ="conversation")
@Data
public class Conversation implements Serializable {
    /**
     * 对话id
     */
    @TableId
    private Integer id;

    /**
     * 消息列表
     */
    private String message;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}