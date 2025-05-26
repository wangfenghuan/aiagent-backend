package com.wfh.aiagent.app;

import com.wfh.aiagent.advisor.MyLoggerAdvisor;
import com.wfh.aiagent.advisor.ReReadingAdvisor;
import com.wfh.aiagent.chatmemory.DbBasedMemory;
import com.wfh.aiagent.chatmemory.FileBasedMemory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * @Author FengHuan Wang
 * @Date 2025/5/25 10:06
 * @Version 1.0
 */
@Component
@Slf4j
public class ProgramingApp {

    private final ChatClient chatClient;

    @Resource
    private VectorStore programingAppVectorStore;


    private static final String SYSTEM_PROMPT_ADVANCE = "你是一名拥有15年经验的首席Java工程师，擅长处理高并发分布式系统及JVM性能调优。请按照以下结构化流程解决问题：\n" +
            "\n" +
            "1. 需求分析阶段\n" +
            "- 确认问题边界和技术约束\n" +
            "- 识别潜在的并发、内存泄漏、GC问题\n" +
            "- 评估O(n)复杂度并提出优化方向\n" +
            "- 列出可能的设计模式应用场景\n" +
            "\n" +
            "2. 架构设计阶段（针对复杂问题）\n" +
            "□ 绘制UML类图/时序图核心要素\n" +
            "□ 选择合适的技术栈组合（如Spring生态组件）\n" +
            "□ 设计分层架构（Controller/Service/DAO）\n" +
            "□ 确定线程模型和并发控制策略\n" +
            "□ 规划监控指标（Metrics/Logging/Tracing）\n" +
            "\n" +
            "3. 代码实现规范\n" +
            "√ 严格遵循Java Effective规范\n" +
            "√ 使用防御性编程和空对象模式\n" +
            "√ 添加必要的Javadoc（包含@param @return @throws）\n" +
            "√ 分离接口与实现（面向接口编程）\n" +
            "√ 采用恰当的异常处理策略（Checked/Unchecked异常区分）\n" +
            "\n" +
            "4. 质量保障要求\n" +
            "▶ 编写JUnit5测试用例（包括正常流、边界条件、异常场景）\n" +
            "▶ 提供压力测试方案（JMeter/Gatling模板）\n" +
            "▶ 添加性能基准测试（JMH示例）\n" +
            "▶ 生成安全审计要点（OWASP TOP10相关检查项）\n" +
            "\n" +
            "5. 交付文档\n" +
            "※ 架构决策记录（ADR）\n" +
            "※ 运维部署手册（含JVM调优参数建议）\n" +
            "※ 监控看板配置说明（Grafana模板ID）\n" +
            "※ 技术债清单和技术演进路线\n" +
            "\n" +
            "当前待解决问题：[用户具体问题]\n" +
            "\n" +
            "请按照以下格式响应：\n" +
            "【架构概要】\n" +
            "用ASCII图形展示核心组件关系\n" +
            "\n" +
            "【代码实现】\n" +
            "展示关键代码段（标注设计模式应用点）\n" +
            "\n" +
            "【质量检查点】\n" +
            "列出必须验证的测试场景\n" +
            "\n" +
            "【性能优化】\n" +
            "提出至少3个JVM层优化建议\n" +
            "\n" +
            "【知识扩展】\n" +
            "推荐2个相关论文/技术文档链接";

    private static final String SYSTEM_PROMPT_DEFAULT = "你是一名资深Java技术专家，专注于高效解决具体编程问题。请按以下结构处理问题：\n" +
            "\n" +
            "【问题澄清】\n" +
            "1. 确认问题的核心矛盾点（明确输入输出）\n" +
            "2. 识别潜在的技术风险（如并发竞态、资源泄漏等）\n" +
            "3. 确定关键约束条件（如JDK版本、性能要求等）\n" +
            "\n" +
            "【解决方案设计】\n" +
            "1. 给出2-3种可行方案及其优劣对比\n" +
            "2. 标注方案适用的场景边界\n" +
            "3. 指出可能的设计模式应用点\n" +
            "\n" +
            "【精准代码实现】\n" +
            "1. 提供最小可行代码段（包含核心逻辑）\n" +
            "2. 使用防御性编程和空安全处理\n" +
            "3. 标注关键算法复杂度\n" +
            "4. 添加必要异常处理\n" +
            "\n" +
            "【验证要点】\n" +
            "1. 列出必须覆盖的测试用例\n" +
            "2. 提供诊断问题的方法（如jstack使用示例）\n" +
            "3. 给出性能验证的快速检查命令\n" +
            "\n" +
            "【深度优化】 \n" +
            "1. JVM层面调优建议（与问题相关）\n" +
            "2. 代码级优化策略（如热点方法优化）\n" +
            "3. 可选扩展方案（如需要长期维护时的改进）\n" +
            "\n" +
            "请避免工程文档输出，聚焦技术问题本身。当前问题：[用户具体问题]";



    // private static final String SYSTEM_PROMPT_DEFAULT = "你好啊";


    record ProgramingReport(String title, List<String> suggestions){

    }

    /**
     * 初始化chatclient
     * @param dashscopeChatModel
     */
    public ProgramingApp(ChatModel dashscopeChatModel, DbBasedMemory chatMemory) {
        // 初始化基于文件的对话记忆
        String fileDir = System.getProperty("user.dir") + "/tmp/.chatmemory";
        // ChatMemory chatMemory = new FileBasedMemory(fileDir);
        // 初始化基于内存的对话记忆
        // ChatMemory chatMemory = new InMemoryChatMemory();
        // ChatMemory chatMemory = new DbBasedMemory();
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT_DEFAULT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        // 自定义日志
                        new MyLoggerAdvisor()
                        //new ReReadingAdvisor()
                ).build();
    }

    /**
     * AI基础对话（支持多轮对话记忆）
     * @param messsage
     * @param chatId
     * @return
     */
    public String doChat(String messsage, String chatId){
        ChatResponse chatResponse = this.chatClient
                .prompt()
                .user(messsage)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content:{}", content);
        return content;
    }

    /**
     * AI编程报告（实战结构化输出）
     * @param messsage
     * @param chatId
     * @return
     */
    public ProgramingReport doChatWithReport(String messsage, String chatId){
        ProgramingReport programingReport = chatClient
                .prompt()
                .user(messsage)
                .system(SYSTEM_PROMPT_DEFAULT + "每次对话后都要生成问题结果报告，标题为{用户名}的编程宝典，内容为建议列表")
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .entity(ProgramingReport.class);
        log.info("programingReport:{}", programingReport);
        return programingReport;
    }

    /**
     * rag知识库问答
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithRag(String message, String chatId){
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志
                .advisors(new MyLoggerAdvisor())
                // 启用rag知识库
                .advisors(new QuestionAnswerAdvisor(programingAppVectorStore))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content:{}", content);
        return content;
    }
}
