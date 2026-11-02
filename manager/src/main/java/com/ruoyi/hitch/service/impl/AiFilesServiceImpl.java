package com.ruoyi.hitch.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.hitch.mapper.AiFilesMapper;
import com.ruoyi.hitch.domain.AiFiles;
import com.ruoyi.hitch.service.IAiFilesService;
import com.ruoyi.common.core.text.Convert;

/**
 * 文件库Service业务层处理
 * 
 * @author Shawn
 * @date 2026-08-21
 */
@Service
public class AiFilesServiceImpl implements IAiFilesService 
{
    @Autowired
    private AiFilesMapper aiFilesMapper;

    /**
     * 查询文件库
     * 
     * @param id 文件库主键
     * @return 文件库
     */
    @Override
    public AiFiles selectAiFilesById(Long id)
    {
        return aiFilesMapper.selectAiFilesById(id);
    }

    /**
     * 查询文件库列表
     * 
     * @param aiFiles 文件库
     * @return 文件库
     */
    @Override
    public List<AiFiles> selectAiFilesList(AiFiles aiFiles)
    {
        return aiFilesMapper.selectAiFilesList(aiFiles);
    }

    /**
     * 新增文件库
     * 
     * @param aiFiles 文件库
     * @return 结果
     */
    @Override
    public int insertAiFiles(AiFiles aiFiles)
    {
        return aiFilesMapper.insertAiFiles(aiFiles);
    }

    /**
     * 修改文件库
     * 
     * @param aiFiles 文件库
     * @return 结果
     */
    @Override
    public int updateAiFiles(AiFiles aiFiles)
    {
        return aiFilesMapper.updateAiFiles(aiFiles);
    }

    /**
     * 批量删除文件库
     * 
     * @param ids 需要删除的文件库主键
     * @return 结果
     */
    @Override
    public int deleteAiFilesByIds(String ids)
    {
        return aiFilesMapper.deleteAiFilesByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除文件库信息
     * 
     * @param id 文件库主键
     * @return 结果
     */
    @Override
    public int deleteAiFilesById(Long id)
    {
        return aiFilesMapper.deleteAiFilesById(id);
    }
}
