package com.zhijiaoyou.aichat.controller;

import com.zhijiaoyou.aichat.service.AiChatService;
import com.zhijiaoyou.aichat.service.AiRateLimiter;
import com.zhijiaoyou.commons.initial.filter.TokenAuthFilter;
import com.zhijiaoyou.modules.vo.AccountVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AiChatControllerTest {
    private AiChatService service;
    private AiRateLimiter limiter;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        service = mock(AiChatService.class);
        limiter = mock(AiRateLimiter.class);
        AiChatController controller = new AiChatController();
        ReflectionTestUtils.setField(controller, "aiChatService", service);
        ReflectionTestUtils.setField(controller, "aiRateLimiter", limiter);
        mvc = MockMvcBuilders.standaloneSetup(controller).addFilters(new TokenAuthFilter()).build();
    }

    @Test
    void anonymousCannotAccessAnyChatEndpointEvenWithForgedHeader() throws Exception {
        mvc.perform(get("/ai/chat/history/cid").header("SESSION_TOKEN_KEY", "user-b"))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/ai/chat/conversations")).andExpect(status().isUnauthorized());
        mvc.perform(get("/ai/chat/messages")).andExpect(status().isUnauthorized());
        mvc.perform(delete("/ai/chat/conversation/cid")).andExpect(status().isUnauthorized());
        mvc.perform(post("/ai/chat/stream").contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\":\"hello\"}"))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(service, limiter);
    }

    @Test
    void historyUsesSessionIdentityInsteadOfForgedHeader() throws Exception {
        when(service.getHistory("cid", "user-a", 1, 50)).thenReturn(List.of());
        mvc.perform(get("/ai/chat/history/cid").session(session("user-a"))
                        .header("SESSION_TOKEN_KEY", "user-b"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
        verify(service).getHistory("cid", "user-a", 1, 50);
    }

    @Test
    void crossUserHistoryReturnsForbidden() throws Exception {
        when(service.getHistory("cid-b", "user-a", 1, 50))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));
        mvc.perform(get("/ai/chat/history/cid-b").session(session("user-a")))
                .andExpect(status().isForbidden());
    }

    @Test
    void crossUserStreamReturnsForbiddenBeforeSseStarts() throws Exception {
        when(limiter.allow("user-a")).thenReturn(true);
        when(service.chatStream("cid-b", "user-a", "hello"))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));
        mvc.perform(post("/ai/chat/stream").session(session("user-a"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"conversationId\":\"cid-b\",\"message\":\"hello\"}"))
                .andExpect(status().isForbidden()).andExpect(request().asyncNotStarted());
    }

    @Test
    void crossUserDeleteReturnsForbidden() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN))
                .when(service).deleteConversation("cid-b", "user-a");
        mvc.perform(delete("/ai/chat/conversation/cid-b").session(session("user-a")))
                .andExpect(status().isForbidden());
    }

    @Test
    void serverConversationIdIsReturnedInResponseHeader() throws Exception {
        when(limiter.allow("user-a")).thenReturn(true);
        when(service.chatStream(null, "user-a", "hello"))
                .thenReturn(new AiChatService.ChatStream("server-cid", Flux.just("hello")));
        mvc.perform(post("/ai/chat/stream").session(session("user-a"))
                        .header("SESSION_TOKEN_KEY", "user-b")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"message\":\"hello\"}"))
                .andExpect(status().isOk()).andExpect(header().string("X-Conversation-Id", "server-cid"));
        verify(service).chatStream(null, "user-a", "hello");
    }

    private MockHttpSession session(String userId) {
        AccountVO user = new AccountVO();
        user.setId(userId);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", user);
        return session;
    }
}
