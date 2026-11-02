package com.zhijiaoyou.commons.initial.filter;

import com.zhijiaoyou.modules.vo.AccountVO;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;

class TokenAuthFilterTest {
    @Test
    void publicLoginRegisterAndDocsRemainAccessible() throws Exception {
        for (String path : new String[]{"/account/api/login", "/account/api/register", "/swagger-ui/index.html"}) {
            MockHttpServletRequest request = new MockHttpServletRequest("GET", path);
            MockFilterChain chain = new MockFilterChain();
            new TokenAuthFilter().doFilter(request, new MockHttpServletResponse(), chain);
            assertSame(request, chain.getRequest());
            assertNull(request.getSession(false));
        }
    }

    @Test
    void protectedRequestsDoNotCreateAnonymousSessions() throws Exception {
        for (String path : new String[]{"/order/api/list", "/ai/chat/stream", "/ai/chat/history/cid"}) {
            MockHttpServletRequest request = new MockHttpServletRequest("GET", path);
            MockHttpServletResponse response = new MockHttpServletResponse();
            MockFilterChain chain = new MockFilterChain();
            new TokenAuthFilter().doFilter(request, response, chain);
            assertEquals(401, response.getStatus());
            assertNull(chain.getRequest());
            assertNull(request.getSession(false));
            assertEquals("application/json;charset=UTF-8", response.getContentType());
        }
    }

    @Test
    void authenticatedUserCanPassFilterWithContextPath() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/app/ai/chat/messages");
        request.setContextPath("/app");
        AccountVO user = new AccountVO();
        user.setId("user-a");
        request.getSession().setAttribute("user", user);
        MockFilterChain chain = new MockFilterChain();
        new TokenAuthFilter().doFilter(request, new MockHttpServletResponse(), chain);
        assertSame(request, chain.getRequest());
    }
}
