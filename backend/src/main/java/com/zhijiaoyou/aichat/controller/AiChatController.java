package com.zhijiaoyou.aichat.controller;

import com.zhijiaoyou.aichat.entity.ChatMessagePO;
import com.zhijiaoyou.aichat.service.AiChatService;
import com.zhijiaoyou.aichat.service.AiRateLimiter;
import com.zhijiaoyou.commons.domin.vo.response.ResponseVO;
import com.zhijiaoyou.modules.vo.AccountVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai/chat")
@Tag(name = "AI聊天", description = "AI 对话接口 — 供「智能问路」页面调用")
public class AiChatController {

    private static final Logger log = LoggerFactory.getLogger(AiChatController.class);

    @Autowired
    private AiChatService aiChatService;

    @Autowired
    private AiRateLimiter aiRateLimiter;

    @Operation(summary = "流式对话 (SSE)")
    @PostMapping("/stream")
    public SseEmitter chatStream(@RequestBody Map<String, String> body, HttpServletRequest request,
                                 HttpServletResponse response) {
        String userId = getUserId(request);
        String message = body.get("message");
        if (message == null || message.trim().isEmpty()) {
            return sseText("请输入你想问的问题～");
        }

        String conversationId = body.get("conversationId");

        // 限流：同一个用户每分钟最多 20 次，防止接口被刷
        if (!aiRateLimiter.allow(userId)) {
            return sseText("你问得有点快啦，稍等一会儿再聊～");
        }

        log.info("AI stream: userId={}, cid={}", userId, conversationId);

        SseEmitter emitter = new SseEmitter(120000L);
        AiChatService.ChatStream stream = aiChatService.chatStream(conversationId, userId, message.trim());
        response.setHeader("X-Conversation-Id", stream.conversationId());
        Flux<String> flux = stream.content();

        flux.subscribe(
            token -> {
                try {
                    emitter.send(SseEmitter.event().data(token));
                } catch (IOException e) {
                    emitter.completeWithError(e);
                }
            },
            emitter::completeWithError,
            emitter::complete
        );

        return emitter;
    }

    @Operation(summary = "获取会话历史消息（分页）")
    @GetMapping("/history/{conversationId}")
    public ResponseVO history(@PathVariable String conversationId,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "50") int size,
                              HttpServletRequest request) {
        List<ChatMessagePO> messages = aiChatService.getHistory(conversationId, getUserId(request), page, size);
        return ResponseVO.success(messages);
    }

    @Operation(summary = "获取用户会话列表")
    @GetMapping("/conversations")
    public ResponseVO conversations(HttpServletRequest request) {
        String userId = getUserId(request);
        List<Map<String, Object>> convs = aiChatService.getConversations(userId);
        return ResponseVO.success(convs);
    }

    @Operation(summary = "删除会话")
    @DeleteMapping("/conversation/{conversationId}")
    public ResponseVO deleteConversation(@PathVariable String conversationId, HttpServletRequest request) {
        aiChatService.deleteConversation(conversationId, getUserId(request));
        return ResponseVO.success(null, "会话已删除");
    }

    @Operation(summary = "获取用户全部聊天记录 (跨会话)")
    @GetMapping("/messages")
    public ResponseVO allMessages(HttpServletRequest request) {
        String userId = getUserId(request);
        List<ChatMessagePO> messages = aiChatService.getAllHistory(userId);
        return ResponseVO.success(messages);
    }

    /**
     * 用 SSE 返回一句普通文本，保证参数错误、限流等场景和前端的解析方式一致。
     */
    private SseEmitter sseText(String text) {
        SseEmitter emitter = new SseEmitter();
        try {
            emitter.send(SseEmitter.event().data(text));
            emitter.complete();
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
        return emitter;
    }

    private String getUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        AccountVO user = session == null ? null : (AccountVO) session.getAttribute("user");
        if (user == null || user.getId() == null || user.getId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请先登录");
        }
        return user.getId();
    }
}
