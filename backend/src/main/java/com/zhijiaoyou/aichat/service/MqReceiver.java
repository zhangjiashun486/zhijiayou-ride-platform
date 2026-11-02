package com.zhijiaoyou.aichat.service;

import com.alibaba.fastjson.JSON;
import com.zhijiaoyou.aichat.handler.MqHandler;
import com.zhijiaoyou.commons.utils.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queuesToDeclare = {
        @Queue("HITCH_AI_MSG"),
        @Queue("HITCH_AI_FILE")})
public class MqReceiver {
    private final static Logger logger = LoggerFactory.getLogger(MqReceiver.class);



    @RabbitHandler
    public void processMessage(String message) {
        logger.info("user hit : message={}", message);
        MessageDto messageDto = JSON.parseObject(message, MessageDto.class);
        logger.info("dto={}",messageDto);

        MqHandler handler = SpringUtil.getBean(messageDto.getType(), MqHandler.class);

        switch (messageDto.getOperation()) {
            case 1:
                handler.add(messageDto.getIds());
                break;
            case 2:
                handler.update(messageDto.getIds());
                break;
            case 3:
                handler.delete(messageDto.getIds());
                break;
            default:
                break;
        }

    }

}
