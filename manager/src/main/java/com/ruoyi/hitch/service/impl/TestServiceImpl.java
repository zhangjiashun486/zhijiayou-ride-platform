package com.ruoyi.hitch.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.hitch.mapper.TestMapper;
import com.ruoyi.hitch.domain.Test;
import com.ruoyi.hitch.service.ITestService;
import com.ruoyi.common.core.text.Convert;

/**
 * 测试Service业务层处理
 * 
 * @author Shawn
 * @date 2026-08-13
 */
@Service
public class TestServiceImpl implements ITestService 
{
    @Autowired
    private TestMapper testMapper;

    /**
     * 查询测试
     * 
     * @param id 测试主键
     * @return 测试
     */
    @Override
    public Test selectTestById(Long id)
    {
        return testMapper.selectTestById(id);
    }

    /**
     * 查询测试列表
     * 
     * @param test 测试
     * @return 测试
     */
    @Override
    public List<Test> selectTestList(Test test)
    {
        return testMapper.selectTestList(test);
    }

    /**
     * 新增测试
     * 
     * @param test 测试
     * @return 结果
     */
    @Override
    public int insertTest(Test test)
    {
        return testMapper.insertTest(test);
    }

    /**
     * 修改测试
     * 
     * @param test 测试
     * @return 结果
     */
    @Override
    public int updateTest(Test test)
    {
        return testMapper.updateTest(test);
    }

    /**
     * 批量删除测试
     * 
     * @param ids 需要删除的测试主键
     * @return 结果
     */
    @Override
    public int deleteTestByIds(String ids)
    {
        return testMapper.deleteTestByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除测试信息
     * 
     * @param id 测试主键
     * @return 结果
     */
    @Override
    public int deleteTestById(Long id)
    {
        return testMapper.deleteTestById(id);
    }
}
