package com.zhijiaoyou.aichat.service;

import com.zhijiaoyou.aichat.entity.ChatMessagePO;
import com.zhijiaoyou.aichat.mapper.ChatMessageMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AiChatServiceTest {
    private ChatMessageMapper mapper;
    private ChatClient client;
    private AiChatService service;

    @BeforeEach
    void setUp() {
        mapper = mock(ChatMessageMapper.class);
        client = mock(ChatClient.class, RETURNS_DEEP_STUBS);
        service = new AiChatService(mapper, client);
    }

    @Test
    void rejectsReadingAnotherUsersConversationBeforeReadingMessages() {
        when(mapper.selectUserIdByConversationId("conversation-b")).thenReturn("user-b");
        assertStatus(HttpStatus.FORBIDDEN,
                () -> service.getHistory("conversation-b", "user-a", 1, 50));
        verify(mapper, never()).selectByConversationIdPaged(anyString(), anyString(), anyInt(), anyInt());
    }

    @Test
    void rejectsContinuingAnotherUsersConversationBeforeSavingOrCallingModel() {
        when(mapper.selectUserIdByConversationId("conversation-b")).thenReturn("user-b");
        assertStatus(HttpStatus.FORBIDDEN,
                () -> service.chatStream("conversation-b", "user-a", "你好"));
        verify(mapper, never()).insert(any());
        verifyNoInteractions(client);
    }

    @Test
    void rejectsDeletingAnotherUsersConversation() {
        when(mapper.selectUserIdByConversationId("conversation-b")).thenReturn("user-b");
        assertStatus(HttpStatus.FORBIDDEN,
                () -> service.deleteConversation("conversation-b", "user-a"));
        verify(mapper, never()).deleteByConversationId(anyString(), anyString());
    }

    @Test
    void rejectsUnknownClientSuppliedConversationId() {
        assertStatus(HttpStatus.NOT_FOUND,
                () -> service.chatStream("made-up-id", "user-a", "你好"));
        verify(mapper, never()).insert(any());
        verifyNoInteractions(client);
    }

    @Test
    void rejectsUnauthenticatedAccessEvenWhenServiceIsCalledDirectly() {
        assertStatus(HttpStatus.UNAUTHORIZED,
                () -> service.getHistory("conversation-a", null, 1, 50));
        assertStatus(HttpStatus.UNAUTHORIZED,
                () -> service.chatStream(null, " ", "你好"));
        assertStatus(HttpStatus.UNAUTHORIZED,
                () -> service.deleteConversation("conversation-a", null));
        assertStatus(HttpStatus.UNAUTHORIZED, () -> service.getConversations(null));
        assertStatus(HttpStatus.UNAUTHORIZED, () -> service.getAllHistory(null));
        verifyNoInteractions(mapper, client);
    }

    @Test
    void ownerCanReadHistoryInDisplayOrderWithPaginationBoundaries() {
        when(mapper.selectUserIdByConversationId("conversation-a")).thenReturn("user-a");
        ChatMessagePO newest = new ChatMessagePO("2", "conversation-a", "user-a", "assistant", "答");
        ChatMessagePO oldest = new ChatMessagePO("1", "conversation-a", "user-a", "user", "问");
        when(mapper.selectByConversationIdPaged("conversation-a", "user-a", 0, 100))
                .thenReturn(new ArrayList<>(List.of(newest, oldest)));
        assertEquals(List.of(oldest, newest), service.getHistory("conversation-a", "user-a", 0, 999));
    }

    @Test
    void ownerCanDeleteOnlyTheirMessages() {
        when(mapper.selectUserIdByConversationId("conversation-a")).thenReturn("user-a");
        service.deleteConversation("conversation-a", "user-a");
        verify(mapper).deleteByConversationId("conversation-a", "user-a");
    }

    @Test
    void newConversationGetsServerIdAndBothMessagesUseAuthenticatedOwner() {
        stubModel();
        when(mapper.selectByConversationId(anyString(), eq("user-a"), eq(50)))
                .thenReturn(new ArrayList<>());
        AiChatService.ChatStream stream = service.chatStream(null, "user-a", "问");
        assertTrue(stream.conversationId().matches("[0-9a-f]{32}"));
        assertEquals(List.of("答"), stream.content().collectList().block());
        var captor = org.mockito.ArgumentCaptor.forClass(ChatMessagePO.class);
        verify(mapper, times(2)).insert(captor.capture());
        for (ChatMessagePO message : captor.getAllValues()) {
            assertEquals("user-a", message.getUserId());
            assertEquals(stream.conversationId(), message.getConversationId());
        }
        assertEquals(List.of("user", "assistant"),
                captor.getAllValues().stream().map(ChatMessagePO::getRole).toList());
    }

    @Test
    void ownerCanContinueExistingConversation() {
        when(mapper.selectUserIdByConversationId("conversation-a")).thenReturn("user-a");
        when(mapper.selectByConversationId("conversation-a", "user-a", 50))
                .thenReturn(new ArrayList<>());
        stubModel();
        AiChatService.ChatStream stream = service.chatStream("conversation-a", "user-a", "问");
        assertEquals("conversation-a", stream.conversationId());
        assertEquals(List.of("答"), stream.content().collectList().block());
    }

    private void stubModel() {
        when(client.prompt().messages(anyList()).user(anyString()).stream().content())
                .thenReturn(Flux.just("答"));
    }

    private void assertStatus(HttpStatus status, org.junit.jupiter.api.function.Executable action) {
        assertEquals(status, assertThrows(ResponseStatusException.class, action).getStatusCode());
    }
}
