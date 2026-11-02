package com.zhijiaoyou.aichat.mapper;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ChatMessageMapperSqlTest {
    @Test
    void readsAndDeletesBindBothConversationAndUserInActualMapperXml() throws Exception {
        Configuration configuration = new Configuration();
        String resource = "mapper/ChatMessageMapper.xml";
        try (var input = Resources.getResourceAsStream(resource)) {
            new XMLMapperBuilder(input, configuration, resource, configuration.getSqlFragments()).parse();
        }
        Map<String, Object> parameters = Map.of("conversationId", "cid", "userId", "user-a",
                "limit", 50, "offset", 0);
        for (String method : new String[]{"selectByConversationId", "selectByConversationIdPaged", "deleteByConversationId"}) {
            var bound = configuration.getMappedStatement(ChatMessageMapper.class.getName() + "." + method)
                    .getBoundSql(parameters);
            String sql = bound.getSql().replaceAll("\\s+", " ");
            assertTrue(sql.contains("WHERE conversation_id = ? AND user_id = ?"), sql);
            assertEquals("conversationId", bound.getParameterMappings().get(0).getProperty());
            assertEquals("userId", bound.getParameterMappings().get(1).getProperty());
        }
    }
}
