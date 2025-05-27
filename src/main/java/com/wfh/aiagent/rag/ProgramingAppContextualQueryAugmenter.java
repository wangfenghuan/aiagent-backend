package com.wfh.aiagent.rag;


import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;

/**
 * @Author FengHuan Wang
 * @Date 2025/5/27 10:10
 * @Version 1.0
 */
public class ProgramingAppContextualQueryAugmenter {
    public static ContextualQueryAugmenter create() {
        PromptTemplate promptTemplate = new PromptTemplate("""
                你应该输出下面的内容：
                抱歉陈琪宝宝，我只能回答你编程相关的问题，没办法帮到你哦~~，
                有问题可以联系你的对象王凤欢，他会给你耐心解答的~~❤❤""");
        return ContextualQueryAugmenter.builder()
                .allowEmptyContext(true)
                .emptyContextPromptTemplate(promptTemplate)
                .build();
    }
}
