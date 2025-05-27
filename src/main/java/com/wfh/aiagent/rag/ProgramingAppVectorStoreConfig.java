package com.wfh.aiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @Author FengHuan Wang
 * @Date 2025/5/26 10:03
 * @Version 1.0
 */
@Configuration
public class ProgramingAppVectorStoreConfig {

    @Resource
    private ProgramingAppDocLoader programingAppDocLoader;

    @Resource
    private MyTokenTextSplitter myTokenTextSplitter;

    @jakarta.annotation.Resource
    private MyKeywordEnricher myKeywordEnricher;

    @Bean
    VectorStore programingAppVectorStore(EmbeddingModel dashscopeEmbeddingModel){
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel).build();
        List<Document> documentList = programingAppDocLoader.loadMds();
        // 自主切分文档
        // List<Document> splitCustomized = myTokenTextSplitter.splitCustomized(documentList);
        // 自动补充关键词元信息
        List<Document> enrichDocuments = myKeywordEnricher.enrichDocuments(documentList);
        simpleVectorStore.add(enrichDocuments);
        return simpleVectorStore;
    }
}
