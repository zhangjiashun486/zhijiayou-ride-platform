package com.ruoyi.hitch.mapper;

import java.util.List;
import com.ruoyi.hitch.domain.AiFiles;

/**
 * 文件库Mapper接口
 * 
 * @author Shawn
 * @date 2026-08-21
 */
public interface AiFilesMapper 
{
    /**
     * 查询文件库
     * 
     * @param id 文件库主键
     * @return 文件库
     */
    public AiFiles selectAiFilesById(Long id);

    /**
     * 查询文件库列表
     * 
     * @param aiFiles 文件库
     * @return 文件库集合
     */
    public List<AiFiles> selectAiFilesList(AiFiles aiFiles);

    /**
     * 新增文件库
     * 
     * @param aiFiles 文件库
     * @return 结果
     */
    public int insertAiFiles(AiFiles aiFiles);

    /**
     * 修改文件库
     * 
     * @param aiFiles 文件库
     * @return 结果
     */
    public int updateAiFiles(AiFiles aiFiles);

    /**
     * 删除文件库
     * 
     * @param id 文件库主键
     * @return 结果
     */
    public int deleteAiFilesById(Long id);

    /**
     * 批量删除文件库
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAiFilesByIds(String[] ids);
}
