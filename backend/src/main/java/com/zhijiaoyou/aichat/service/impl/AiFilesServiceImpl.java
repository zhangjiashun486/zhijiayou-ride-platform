package com.zhijiaoyou.aichat.service.impl;

import com.zhijiaoyou.aichat.entity.po.AiFiles;
import com.zhijiaoyou.aichat.mapper.AiFilesMapper;
import com.zhijiaoyou.aichat.service.IAiFilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 文件库 服务实现
 */
@Service
public class AiFilesServiceImpl implements IAiFilesService {

    @Autowired
    private AiFilesMapper aiFilesMapper;

    @Override
    public AiFiles getById(Integer id) {
        return aiFilesMapper.selectById(id);
    }

    @Override
    public List<AiFiles> listAll() {
        return aiFilesMapper.selectAll();
    }

    @Override
    public boolean save(AiFiles record) {
        return aiFilesMapper.insert(record) > 0;
    }

    @Override
    public boolean updateById(AiFiles record) {
        return aiFilesMapper.updateById(record) > 0;
    }

    @Override
    public boolean removeById(Integer id) {
        return aiFilesMapper.deleteById(id) > 0;
    }
}
