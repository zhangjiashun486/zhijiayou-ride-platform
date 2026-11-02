package com.zhijiaoyou.aichat.service.impl;

import com.zhijiaoyou.aichat.entity.po.AiVectorIds;
import com.zhijiaoyou.aichat.mapper.AiVectorIdsMapper;
import com.zhijiaoyou.aichat.service.IAiVectorIdsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 向量ID映射 服务实现
 */
@Service
public class AiVectorIdsServiceImpl implements IAiVectorIdsService {

    @Autowired
    private AiVectorIdsMapper mapper;

    @Override
    public AiVectorIds getByTypeAndSourceId(String type, String sourceId) {
        return mapper.selectByTypeAndSourceId(type, sourceId);
    }

    @Override
    public List<AiVectorIds> listByTypeAndSourceId(String type, String sourceId) {
        return mapper.selectListByTypeAndSourceId(type, sourceId);
    }

    @Override
    public List<AiVectorIds> listByType(String type) {
        return mapper.selectByType(type);
    }

    @Override
    public boolean save(AiVectorIds record) {
        return mapper.insert(record) > 0;
    }

    @Override
    public boolean removeByDocumentId(String documentId) {
        return mapper.deleteByDocumentId(documentId) > 0;
    }
}
