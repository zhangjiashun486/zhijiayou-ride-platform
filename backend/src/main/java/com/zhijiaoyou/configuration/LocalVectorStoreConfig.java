package com.zhijiaoyou.configuration;

import com.zhijiaoyou.aichat.entity.po.AiMsg;
import com.zhijiaoyou.aichat.service.IAiMsgService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.Map;

/**
 * 本地开发环境的向量库配置。
 *
 * <p>线上/课程环境使用 Redis Stack 的 RediSearch 做向量检索；本地电脑没有 Redis Stack 时，
 * 改用 Spring AI 自带的内存向量库 SimpleVectorStore，并在启动时把资讯表的文章加载进去，
 * 这样 RAG 链路（后台录资讯 -> 向量检索 -> AI 回答）在本地依然可以演示。</p>
 */
@Configuration
@Profile("local")
public class LocalVectorStoreConfig {

    private static final Logger log = LoggerFactory.getLogger(LocalVectorStoreConfig.class);

    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }

    @Bean
    public ApplicationRunner loadKnowledgeBase(VectorStore vectorStore, IAiMsgService aiMsgService) {
        return args -> {
            try {
                List<AiMsg> messages = aiMsgService.list(new AiMsg());
                if (messages == null || messages.isEmpty()) {
                    log.info("本地向量库：资讯表为空，跳过加载");
                    return;
                }
                List<Document> documents = messages.stream()
                        .map(item -> new Document(
                                "ai-msg-" + item.getId(),
                                buildText(item),
                                Map.of("source", "ai_msg", "id", String.valueOf(item.getId()))))
                        .toList();
                vectorStore.add(documents);
                log.info("本地向量库加载完成，共 {} 条资讯", documents.size());
            } catch (Exception e) {
                // 加载失败（比如大模型 key 无效）不影响项目启动，只影响知识库检索
                log.warn("本地向量库加载失败：{}", e.getMessage());
            }
        };
    }

    private String buildText(AiMsg item) {
        StringBuilder sb = new StringBuilder();
        if (item.getTitle() != null && !item.getTitle().isEmpty()) {
            sb.append("标题：").append(item.getTitle()).append('\n');
        }
        if (item.getDictLabel() != null && !item.getDictLabel().isEmpty()) {
            sb.append("分类：").append(item.getDictLabel()).append('\n');
        }
        if (item.getContent() != null) {
            sb.append(item.getContent());
        }
        return sb.toString();
    }
}
