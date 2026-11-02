package com.zhijiaoyou.aichat.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * AI 对话限流：同一个用户每分钟最多 20 次请求。
 * 使用 Redis 原子自增计数，多实例部署时各节点共享同一份计数。
 */
@Component
public class AiRateLimiter {

    private static final Logger log = LoggerFactory.getLogger(AiRateLimiter.class);
    private static final int MAX_REQUESTS_PER_MINUTE = 20;
    private static final String KEY_PREFIX = "ai:chat:limit:";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public boolean allow(String userId) {
        // 用分钟时间戳做 key 后缀，天然实现“每分钟重置”
        String key = KEY_PREFIX + userId + ":" + (System.currentTimeMillis() / 60_000);
        Long count = stringRedisTemplate.opsForValue().increment(key);
        if (count != null && count == 1L) {
            stringRedisTemplate.expire(key, Duration.ofSeconds(70));
        }
        boolean allowed = count == null || count <= MAX_REQUESTS_PER_MINUTE;
        if (!allowed) {
            log.warn("AI 请求触发限流: userId={}, count={}", userId, count);
        }
        return allowed;
    }
}
