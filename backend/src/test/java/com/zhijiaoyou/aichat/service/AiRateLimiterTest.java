package com.zhijiaoyou.aichat.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiRateLimiterTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private AiRateLimiter aiRateLimiter;

    private AtomicLong counter;

    @BeforeEach
    void setUp() {
        counter = new AtomicLong();
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenAnswer(invocation -> counter.incrementAndGet());
    }

    @Test
    void allowsFirstTwentyRequestsPerWindow() {
        for (int i = 0; i < 20; i++) {
            assertTrue(aiRateLimiter.allow("user-1"));
        }
    }

    @Test
    void rejectsTwentyFirstRequestInSameWindow() {
        for (int i = 0; i < 20; i++) {
            aiRateLimiter.allow("user-1");
        }

        assertFalse(aiRateLimiter.allow("user-1"));
        verify(stringRedisTemplate, times(1)).expire(anyString(), any(Duration.class));
    }
}