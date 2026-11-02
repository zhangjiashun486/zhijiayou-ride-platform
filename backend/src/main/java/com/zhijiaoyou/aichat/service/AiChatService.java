package com.zhijiaoyou.aichat.service;

import com.zhijiaoyou.aichat.entity.ChatMessagePO;
import com.zhijiaoyou.aichat.mapper.ChatMessageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.*;

/**
 * AI 聊天服务 — Spring AI 1.1.2 + 通义千问 (DashScope OpenAI 兼容)
 */
@Service
public class AiChatService {

    private static final Logger log = LoggerFactory.getLogger(AiChatService.class);
    private static final int MAX_HISTORY = 20;

    private final ChatMessageMapper chatMessageMapper;
    private final ChatClient chatClient;

    public AiChatService(ChatMessageMapper chatMessageMapper, ChatClient chatClient) {
        this.chatMessageMapper = chatMessageMapper;
        this.chatClient = chatClient;
    }

    public record ChatStream(String conversationId, Flux<String> content) {}

    //  任务4.2.3 - 完成ChatClient配置
    public ChatStream chatStream(String conversationId, String userId, String userMessage) {
        requireUser(userId);
        // 新会话由服务器分配 ID，已有会话必须先检查归属。
        conversationId = resolveConversationId(conversationId, userId);
        final String cid = conversationId;
        //查询最近的20条记录  参考buildHistory() ???为什么不用getHistory
        List<Message> history = buildHistory(cid, userId);
        //保存新的用户消息到历史记录表（角色：user）
        saveMessage(cid,userId,"user",userMessage);

        //收集流式输出的片段，最后整体给doOnComplete存库用
        StringBuilder sb = new StringBuilder();
        //拼接到chatClient
        Flux<String> content = chatClient.prompt()
                .messages(history)   //历史消息
                .user(userMessage)   //用户当前消息
            .stream()
            .content()
            .map(chunk -> {
                //流式输出的每个小片段
                //需要使用外部变量收集这些小片段，最后整体给doOnComplete存库用
                sb.append(chunk);

                return chunk != null ? chunk : "";
            })
            .doOnComplete(() -> {
                //全部流式输出完成后的动作：保存新的消息到历史记录表（角色：assistant）
                saveMessage(cid,userId,"assistant",sb.toString());
                log.info("流式输出完成: cid={}", cid);
            })
            .onErrorResume(err -> {
                log.warn("AI 流式调用失败: cid={}", cid, err);
                return Flux.just("抱歉，AI 服务暂时不可用。");
            });
        return new ChatStream(cid, content);
    }

    /**
     * 分页查询会话历史：数据库按时间倒序取一页，再反转成时间升序返回给前端展示。
     */
    public List<ChatMessagePO> getHistory(String conversationId, String userId, int page, int size) {
        requireConversationOwner(conversationId, userId);
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 100);
        List<ChatMessagePO> records = chatMessageMapper.selectByConversationIdPaged(
                conversationId, userId, (safePage - 1) * safeSize, safeSize);
        Collections.reverse(records);
        return records;
    }

    /**
     * 删除会话：先校验会话归属，避免删到别人的聊天记录。
     */
    public void deleteConversation(String conversationId, String userId) {
        requireConversationOwner(conversationId, userId);
        chatMessageMapper.deleteByConversationId(conversationId, userId);
    }

    public List<Map<String, Object>> getConversations(String userId) {
        requireUser(userId);
        return chatMessageMapper.selectConversationIdsByUser(userId);
    }

    public List<ChatMessagePO> getAllHistory(String userId) {
        requireUser(userId);
        return chatMessageMapper.selectAllByUserId(userId);
    }

    // ---- 内部 ----

    private String resolveConversationId(String cid, String userId) {
        if (cid == null || cid.isBlank()) {
            return UUID.randomUUID().toString().replace("-", "");
        }
        requireConversationOwner(cid, userId);
        return cid;
    }

    private void requireConversationOwner(String cid, String userId) {
        requireUser(userId);
        String owner = chatMessageMapper.selectUserIdByConversationId(cid);
        if (owner == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "会话不存在");
        }
        if (!owner.equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权操作该会话");
        }
    }

    private void requireUser(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请先登录");
        }
    }

    //保存对话记录到mysql
    private void saveMessage(String cid, String uid, String role, String content) {
        chatMessageMapper.insert(new ChatMessagePO(
                UUID.randomUUID().toString().replace("-", ""), cid, uid, role, content));
    }

    //  任务4.2.4 - 完成会话上下文封装
    private List<Message> buildHistory(String conversationId, String userId) {
        //根据conversationId，使用chatMessageMapper查询历史消息
        List<ChatMessagePO> records = chatMessageMapper.selectByConversationId(conversationId, userId, 50);

        //遍历返回的列表，逐个检查消息的role

        //注意返回的ChatMessagePO.role决定了你要使用哪种消息对象封装返回

        //user -> UserMessage
        //assistant -> AssistantMessage
        List<Message> history = new ArrayList<>();
        int start = Math.max(0, records.size() - MAX_HISTORY);
        Collections.reverse(records);
        for (int i = start; i < records.size(); i++) {
            ChatMessagePO po = records.get(i);
            if ("user".equals(po.getRole())) {
                history.add(new UserMessage(po.getContent()));
            } else {
                history.add(new AssistantMessage(po.getContent()));
            }
        }
        //隐藏小技巧：history会话上下文是有长度限制的，建议20条


        return history;
    }

}
