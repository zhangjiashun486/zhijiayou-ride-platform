-- ================================
-- AI 聊天消息表
-- 用于存储与通义千问的对话记录（会话记忆）
-- ================================
CREATE TABLE IF NOT EXISTS t_chat_message (
    id              VARCHAR(64)    NOT NULL COMMENT '主键',
    conversation_id VARCHAR(64)    NOT NULL COMMENT '会话ID',
    user_id         VARCHAR(64)    NOT NULL COMMENT '用户ID',
    role            VARCHAR(16)    NOT NULL COMMENT '角色: user / assistant / system',
    content         TEXT           NOT NULL COMMENT '消息内容',
    created_time    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    INDEX idx_conversation_id (conversation_id),
    INDEX idx_user_id (user_id),
    INDEX idx_created_time (created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI聊天消息记录';
