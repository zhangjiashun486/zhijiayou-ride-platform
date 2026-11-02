package com.ruoyi.hitch.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.hitch.mapper.AiMsgMapper;
import com.ruoyi.hitch.domain.AiMsg;
import com.ruoyi.hitch.service.IAiMsgService;
import com.ruoyi.common.core.text.Convert;

/**
 * 资讯库Service业务层处理
 * 
 * @author Shawn
 * @date 2026-08-13
 */
@Service
public class AiMsgServiceImpl implements IAiMsgService 
{
    @Autowired
    private AiMsgMapper aiMsgMapper;

    /**
     * 查询资讯库
     * 
     * @param id 资讯库主键
     * @return 资讯库
     */
    @Override
    public AiMsg selectAiMsgById(Long id)
    {
        return aiMsgMapper.selectAiMsgById(id);
    }

    /**
     * 查询资讯库列表
     * 
     * @param aiMsg 资讯库
     * @return 资讯库
     */
    @Override
    public List<AiMsg> selectAiMsgList(AiMsg aiMsg)
    {
        return aiMsgMapper.selectAiMsgList(aiMsg);
    }

    /**
     * 新增资讯库
     * 
     * @param aiMsg 资讯库
     * @return 结果
     */
    @Override
    public int insertAiMsg(AiMsg aiMsg)
    {
        aiMsg.setCreateTime(DateUtils.getNowDate());
        return aiMsgMapper.insertAiMsg(aiMsg);
    }

    /**
     * 修改资讯库
     * 
     * @param aiMsg 资讯库
     * @return 结果
     */
    @Override
    public int updateAiMsg(AiMsg aiMsg)
    {
        aiMsg.setUpdateTime(DateUtils.getNowDate());
        return aiMsgMapper.updateAiMsg(aiMsg);
    }

    /**
     * 批量删除资讯库
     * 
     * @param ids 需要删除的资讯库主键
     * @return 结果
     */
    @Override
    public int deleteAiMsgByIds(String ids)
    {
        return aiMsgMapper.deleteAiMsgByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除资讯库信息
     * 
     * @param id 资讯库主键
     * @return 结果
     */
    @Override
    public int deleteAiMsgById(Long id)
    {
        return aiMsgMapper.deleteAiMsgById(id);
    }
}
