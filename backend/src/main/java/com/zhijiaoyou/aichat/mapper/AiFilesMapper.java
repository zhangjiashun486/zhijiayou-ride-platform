package com.zhijiaoyou.aichat.mapper;

import com.zhijiaoyou.aichat.entity.po.AiFiles;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文件库 Mapper
 */
public interface AiFilesMapper {

    int insert(AiFiles record);

    AiFiles selectById(@Param("id") Integer id);

    List<AiFiles> selectAll();

    int updateById(AiFiles record);

    int deleteById(@Param("id") Integer id);
}
