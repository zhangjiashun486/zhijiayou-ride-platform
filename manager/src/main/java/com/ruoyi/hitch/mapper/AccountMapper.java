package com.ruoyi.hitch.mapper;

import java.util.List;
import com.ruoyi.hitch.domain.Account;

/**
 * 用户Mapper接口
 * 
 * @author Shawn
 * @date 2026-08-13
 */
public interface AccountMapper 
{
    /**
     * 查询用户
     * 
     * @param id 用户主键
     * @return 用户
     */
    public Account selectAccountById(String id);

    /**
     * 查询用户列表
     * 
     * @param account 用户
     * @return 用户集合
     */
    public List<Account> selectAccountList(Account account);

    /**
     * 新增用户
     * 
     * @param account 用户
     * @return 结果
     */
    public int insertAccount(Account account);

    /**
     * 修改用户
     * 
     * @param account 用户
     * @return 结果
     */
    public int updateAccount(Account account);

    /**
     * 删除用户
     * 
     * @param id 用户主键
     * @return 结果
     */
    public int deleteAccountById(String id);

    /**
     * 批量删除用户
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAccountByIds(String[] ids);
}
