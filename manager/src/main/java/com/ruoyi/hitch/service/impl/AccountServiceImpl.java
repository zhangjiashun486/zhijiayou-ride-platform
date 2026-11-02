package com.ruoyi.hitch.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.hitch.mapper.AccountMapper;
import com.ruoyi.hitch.domain.Account;
import com.ruoyi.hitch.service.IAccountService;
import com.ruoyi.common.core.text.Convert;

/**
 * 用户Service业务层处理
 * 
 * @author Shawn
 * @date 2026-08-13
 */
@Service
public class AccountServiceImpl implements IAccountService 
{
    @Autowired
    private AccountMapper accountMapper;

    /**
     * 查询用户
     * 
     * @param id 用户主键
     * @return 用户
     */
    @Override
    public Account selectAccountById(String id)
    {
        return accountMapper.selectAccountById(id);
    }

    /**
     * 查询用户列表
     * 
     * @param account 用户
     * @return 用户
     */
    @Override
    public List<Account> selectAccountList(Account account)
    {
        return accountMapper.selectAccountList(account);
    }

    /**
     * 新增用户
     * 
     * @param account 用户
     * @return 结果
     */
    @Override
    public int insertAccount(Account account)
    {
        return accountMapper.insertAccount(account);
    }

    /**
     * 修改用户
     * 
     * @param account 用户
     * @return 结果
     */
    @Override
    public int updateAccount(Account account)
    {
        return accountMapper.updateAccount(account);
    }

    /**
     * 批量删除用户
     * 
     * @param ids 需要删除的用户主键
     * @return 结果
     */
    @Override
    public int deleteAccountByIds(String ids)
    {
        return accountMapper.deleteAccountByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除用户信息
     * 
     * @param id 用户主键
     * @return 结果
     */
    @Override
    public int deleteAccountById(String id)
    {
        return accountMapper.deleteAccountById(id);
    }
}
