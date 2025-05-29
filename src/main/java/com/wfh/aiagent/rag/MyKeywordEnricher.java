package com.wfh.aiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.KeywordMetadataEnricher;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author FengHuan Wang
 * @Date 2025/5/27 8:46
 * @Version 1.0
 */
@Component
public class MyKeywordEnricher {

    @Resource
    private ChatModel dashscopeChatModel;

    public List<Document> enrichDocuments(List<Document> documents) {
        return new KeywordMetadataEnricher(dashscopeChatModel, 5).apply(documents);
    }

}
