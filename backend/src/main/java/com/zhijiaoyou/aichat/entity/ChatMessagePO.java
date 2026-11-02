package com.zhijiaoyou.aichat.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * AI 聊天消息实体 — 持久化到 MySQL t_chat_message
 */
public class ChatMessagePO implements Serializable {

    private String id;
    private String conversationId;   // 会话ID
    private String userId;           // 用户ID
    private String role;             // user / assistant / system
    private String content;          // 消息内容
    private Date createdTime;

    public ChatMessagePO() {}

    public ChatMessagePO(String id, String conversationId, String userId, String role, String content) {
        this.id = id;
        this.conversationId = conversationId;
        this.userId = userId;
        this.role = role;
        this.content = content;
        this.createdTime = new Date();
    }

    // ---- getters / setters ----

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Date getCreatedTime() { return createdTime; }
    public void setCreatedTime(Date createdTime) { this.createdTime = createdTime; }
}
