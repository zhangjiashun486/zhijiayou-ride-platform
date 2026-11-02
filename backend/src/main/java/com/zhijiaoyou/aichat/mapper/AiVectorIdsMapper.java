package com.zhijiaoyou.aichat.mapper;

import com.zhijiaoyou.aichat.entity.po.AiVectorIds;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 向量ID映射 Mapper
 */
public interface AiVectorIdsMapper {

    int insert(AiVectorIds record);

    AiVectorIds selectById(@Param("id") Integer id);

    /**
     * 根据业务类型和源ID查找
     */
    AiVectorIds selectByTypeAndSourceId(@Param("type") String type, @Param("sourceId") String sourceId);

    /**
     * 根据业务类型和源ID查找所有记录（一个文件可能拆成多个文档段）
     */
    List<AiVectorIds> selectListByTypeAndSourceId(@Param("type") String type, @Param("sourceId") String sourceId);

    List<AiVectorIds> selectByType(@Param("type") String type);

    int deleteById(@Param("id") Integer id);

    int deleteByDocumentId(@Param("documentId") String documentId);
}
