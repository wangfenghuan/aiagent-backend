package com.wfh.aiagent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author FengHuan Wang
 * @Date 2025/5/26 9:38
 * @Version 1.0
 */
@Component
@Slf4j
public class ProgramingAppDocLoader {

    private final ResourcePatternResolver resourcePatternResolver;

    public ProgramingAppDocLoader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    /**
     * 加载多篇文档
     * @return doc
     */
    public List<Document> loadMds(){
        List<Document> documents = new ArrayList<>();
        // 加载多篇md文档
        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath*:doc/*.md");
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                MarkdownDocumentReaderConfig readerConfig = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true)
                        .withIncludeBlockquote(false)
                        .withIncludeCodeBlock(false)
                        .withAdditionalMetadata("filename", filename)
                        .build();
                MarkdownDocumentReader markdownDocumentReader = new MarkdownDocumentReader(resource, readerConfig);
                documents.addAll(markdownDocumentReader.get());
            }

        } catch (IOException e) {
            log.error("文档加载失败");
        }
        return documents;
    }
}
