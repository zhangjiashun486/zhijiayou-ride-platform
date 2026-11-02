package com.zhijiaoyou.configuration;

import com.zhijiaoyou.aichat.service.WeatherService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringAIConfig {

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private ChatModel chatModel;

    @Autowired
    private WeatherService weatherService;

    //以下任务均需要同步调整SYSTEM_PROMPT和chatClient代码，搭配调试
    //任务4.2.2 - chatClient集成defaultSystem，给他个名字角色，让它知道自己可以做什么
    //任务5.2.3 - chatClient集成defaultAdvisors（QuestionAnswerAdvisor），告诉他当用户询问旅行社之类的资讯时，从知识库读取
    //任务6.2.3 - 优化提示词，告诉他当用户询问景区之类的百科时，从知识库读取
    //任务7.2.3 - chatClient集成defaultTools，告诉他当用户询问天气时，调用xxx工具查询天气
    static final String SYSTEM_PROMPT =
            """
                    你是智驾游的ai助手，叫“小驾“。
                    职责：
                    1. 回答旅行社、旅游资讯、出行攻略、景区百科类问题，优先依据系统提供的知识库内容，资料不足时明确说“资料库里暂时没有”。
                    2. 用户询问城市天气时，调用天气工具查询，再根据返回结果回答。
                    3. 保持简洁、口语化，不编造票价、营业时间、路线等具体信息。
                    4.在遇到景区知识资讯时，读取向量库
                    """;

    // TODO: 任务4~7，需要同步调整ChatClient对象，集成大模型、提示词、向量库查询、天气插件
    @Bean
    public ChatClient chatClient() {
        return ChatClient.builder(chatModel)
                .defaultSystem(SYSTEM_PROMPT) //提示词
//                .defaultXXXX  代码在这里增强
                .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore)
                        .searchRequest(SearchRequest.builder()
                                .topK(5)
                                .similarityThreshold(0.6)
                                .build())
                        .build())
                .defaultTools(weatherService)
                .build();
    }

}
