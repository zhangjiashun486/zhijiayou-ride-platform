package com.ruoyi.framework.web.service;

import com.alibaba.fastjson.JSON;
import com.ruoyi.common.core.domain.MessageDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitSendService {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    public void sendMessage(String ids, int operation, String type) {
        MessageDto messageDto = MessageDto.builder()
                .ids(ids)
                .operation(operation)
                .type(type)
                .build();
        rabbitTemplate.convertAndSend(type, JSON.toJSONString(messageDto));
    }
    public void sendAddMsg(String ids){
        sendMessage(ids,1,"HITCH_AI_MSG");
    }
    public void sendUpdateMsg(String ids){
        sendMessage(ids,2,"HITCH_AI_MSG");
    }
    public void sendDeleteMsg(String ids){
        sendMessage(ids,3,"HITCH_AI_MSG");
    }
    public void sendAddFile(String ids){
        sendMessage(ids,1,"HITCH_AI_FILE");
    }
    public void sendUpdateFile(String ids){
        sendMessage(ids,2,"HITCH_AI_FILE");
    }
    public void sendDeleteFile(String ids){
        sendMessage(ids,3,"HITCH_AI_FILE");
    }
}
