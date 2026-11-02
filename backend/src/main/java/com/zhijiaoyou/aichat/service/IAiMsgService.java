package com.zhijiaoyou.aichat.service;

import com.zhijiaoyou.aichat.entity.po.AiMsg;

import java.util.List;

/**
 * 资讯库 服务接口
 */
public interface IAiMsgService {

    AiMsg getById(Integer id);

    List<AiMsg> list(AiMsg condition);

    boolean save(AiMsg record);

    boolean updateById(AiMsg record);

    boolean removeById(Integer id);
}
