package com.ruoyi.hitch.service;

import java.util.List;
import com.ruoyi.hitch.domain.AiMsg;

/**
 * 资讯库Service接口
 * 
 * @author Shawn
 * @date 2026-08-13
 */
public interface IAiMsgService 
{
    /**
     * 查询资讯库
     * 
     * @param id 资讯库主键
     * @return 资讯库
     */
    public AiMsg selectAiMsgById(Long id);

    /**
     * 查询资讯库列表
     * 
     * @param aiMsg 资讯库
     * @return 资讯库集合
     */
    public List<AiMsg> selectAiMsgList(AiMsg aiMsg);

    /**
     * 新增资讯库
     * 
     * @param aiMsg 资讯库
     * @return 结果
     */
    public int insertAiMsg(AiMsg aiMsg);

    /**
     * 修改资讯库
     * 
     * @param aiMsg 资讯库
     * @return 结果
     */
    public int updateAiMsg(AiMsg aiMsg);

    /**
     * 批量删除资讯库
     * 
     * @param ids 需要删除的资讯库主键集合
     * @return 结果
     */
    public int deleteAiMsgByIds(String ids);

    /**
     * 删除资讯库信息
     * 
     * @param id 资讯库主键
     * @return 结果
     */
    public int deleteAiMsgById(Long id);
}
