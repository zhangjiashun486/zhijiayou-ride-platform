package com.zhijiaoyou.aichat.service;

import com.zhijiaoyou.aichat.entity.po.AiFiles;

import java.util.List;

/**
 * 文件库 服务接口
 */
public interface IAiFilesService {

    AiFiles getById(Integer id);

    List<AiFiles> listAll();

    boolean save(AiFiles record);

    boolean updateById(AiFiles record);

    boolean removeById(Integer id);
}
