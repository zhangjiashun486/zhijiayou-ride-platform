package com.zhijiaoyou.aichat.mapper;

import com.zhijiaoyou.aichat.entity.po.AiMsg;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 资讯库 Mapper
 */
public interface AiMsgMapper {

    int insert(AiMsg record);

    AiMsg selectById(@Param("id") Integer id);

    List<AiMsg> selectList(AiMsg condition);

    int updateById(AiMsg record);

    int deleteById(@Param("id") Integer id);
}
