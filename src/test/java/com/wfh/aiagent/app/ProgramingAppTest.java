package com.wfh.aiagent.app;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.nacos.shaded.io.grpc.NameResolver;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author FengHuan Wang
 * @Date 2025/5/25 14:18
 * @Version 1.0
 */
@SpringBootTest
class ProgramingAppTest {

    @Mock
    private ProgramingApp programingApp;

    @Test
    void test(){
        String string1 = RandomUtil.randomNumbers(5);
        String string = programingApp.doChat("怎么使用hutool进行Java对象的序列化", string1);
        String string2 = programingApp.doChat("我现在想用gson代替hutool", string1);
        String string3 = programingApp.doChat("给我总结一下两者各自优缺点", string1);
        System.out.println(string);
        System.out.println(string2);
        System.out.println(string3);
    }

    @Resource
    VectorStore pgVectorVectorStore;

    @Test
    void test2() {
        List<Document> documents = List.of(
                new Document("Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!!", Map.of("meta1", "meta1")),
                new Document("The World is Big and Salvation Lurks Around the Corner"),
                new Document("You wal forward facing the past and you turn back toward the future.", Map.of("meta2", "meta2")));
        // 添加文档
        pgVectorVectorStore.add(documents);
        // 相似度查询
        List<Document> results = pgVectorVectorStore.similaritySearch(SearchRequest.builder().query("Spring").topK(5).build());
        Assertions.assertNotNull(results);
    }
}