package com.wfh.aiagent.rag;

import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;

/**
 * @Author FengHuan Wang
 * @Date 2025/5/27 9:39
 * @Version 1.0
 */
public class ProgramingAppRagCustomAdvisorFactory {

    /**
     * 创建自定义的RAG增强服务
     * @param vectorStore
     * @param status
     * @return
     */
    public static Advisor createRagCustomAdvisor(VectorStore vectorStore, String status) {
        Filter.Expression expression = new FilterExpressionBuilder()
                .eq("status", status)
                .build();
        VectorStoreDocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                // 过滤条件
                .filterExpression(expression)
                // 相似度阈值
                .similarityThreshold(0.5)
                // 返回文档数量
                .topK(10)
                .build();
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
                .queryAugmenter(ProgramingAppContextualQueryAugmenter.create())
                .build();
    }
}
