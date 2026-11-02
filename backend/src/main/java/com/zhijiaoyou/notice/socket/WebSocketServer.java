package com.zhijiaoyou.notice.socket;


import com.alibaba.fastjson.JSON;
import com.zhijiaoyou.commons.constant.HtichConstants;
import com.zhijiaoyou.commons.domin.vo.response.ResponseVO;
import com.zhijiaoyou.commons.entity.SessionContext;
import com.zhijiaoyou.commons.enums.BusinessErrors;
import com.zhijiaoyou.commons.utils.SpringUtil;
import com.zhijiaoyou.modules.po.StrokePO;
import com.zhijiaoyou.modules.vo.NoticeVO;
import com.zhijiaoyou.notice.handler.NoticeHandler;
import com.zhijiaoyou.storage.service.StrokeAPIService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint(value = "/notice/ws/socket")
public class WebSocketServer {


    //concurrent包的线程安全Map，用来存放每个客户端对应的WebSocketServer对象。
    public final static Map<String, Session> sessionPools = new ConcurrentHashMap<>();

    /*
        用户发送ws消息，message里包含了要发送给谁
    */
    @OnMessage
    public void onMessage(Session session, String message) {
        String accountId = getAccountId(session);
        if (StringUtils.isEmpty(accountId)) {
            return;
        }
        NoticeVO noticeVO = JSON.parseObject(message, NoticeVO.class);
        noticeVO.setSenderId(accountId);
        NoticeHandler noticeHandler = SpringUtil.getBean(NoticeHandler.class);
        if (null != noticeHandler) {
            boolean sendOK = noticeHandler.saveNotice(noticeVO);
            if (!sendOK) {
                ResponseVO responseVO = ResponseVO.error(BusinessErrors.WS_SEND_FAILED);
                try {
                    session.getBasicRemote().sendText(JSON.toJSONString(responseVO));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 连接建立成功调用
     *
     * @param session 客户端与socket建立的会话
     * @param session 客户端的userId
     */
    @OnOpen
    public void onOpen(Session session) {
        String accountId = getAccountId(session);
        if (StringUtils.isEmpty(accountId)) {
            return;
        }
        sessionPools.remove(accountId);
        sessionPools.put(accountId, session);
    }

    /**
     * 关闭连接时调用
     *
     * @param session 关闭连接的客户端的姓名
     */
    @OnClose
    public void onClose(Session session) {
        String accountId = getAccountId(session);
        if (StringUtils.isEmpty(accountId)) {
            return;
        }
        sessionPools.remove(accountId);
    }


    /**
     * 发生错误时候
     *
     * @param session
     * @param throwable
     */
    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("发生错误");
        throwable.printStackTrace();
    }



    /*
    * 在当前session中获取用户accoutId
    * */
    private String getAccountId(Session session) {
        Map<String,List<String>> map = session.getRequestParameterMap();
        List<String> tokens = map.get("SESSION_TOKEN_KEY");
        return tokens == null ? null : tokens.get(0);
    }

}
