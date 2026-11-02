package com.ruoyi.hitch.controller;

import java.util.List;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.hitch.domain.Account;
import com.ruoyi.hitch.service.IAccountService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 用户Controller
 * 
 * @author Shawn
 * @date 2026-08-13
 */
@Controller
@RequestMapping("/hitch/account")
public class AccountController extends BaseController
{
    private String prefix = "hitch/account";

    @Autowired
    private IAccountService accountService;

    @RequiresPermissions("hitch:account:view")
    @GetMapping()
    public String account()
    {
        return prefix + "/account";
    }

    /**
     * 查询用户列表
     */
    @RequiresPermissions("hitch:account:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(Account account)
    {
        startPage();
        List<Account> list = accountService.selectAccountList(account);
        return getDataTable(list);
    }

    /**
     * 导出用户列表
     */
    @RequiresPermissions("hitch:account:export")
    @Log(title = "用户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(Account account)
    {
        List<Account> list = accountService.selectAccountList(account);
        ExcelUtil<Account> util = new ExcelUtil<Account>(Account.class);
        return util.exportExcel(list, "用户数据");
    }

    /**
     * 新增用户
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存用户
     */
    @RequiresPermissions("hitch:account:add")
    @Log(title = "用户", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(Account account)
    {
        return toAjax(accountService.insertAccount(account));
    }

    /**
     * 修改用户
     */
    @RequiresPermissions("hitch:account:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") String id, ModelMap mmap)
    {
        Account account = accountService.selectAccountById(id);
        mmap.put("account", account);
        return prefix + "/edit";
    }

    /**
     * 修改保存用户
     */
    @RequiresPermissions("hitch:account:edit")
    @Log(title = "用户", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(Account account)
    {
        return toAjax(accountService.updateAccount(account));
    }

    /**
     * 删除用户
     */
    @RequiresPermissions("hitch:account:remove")
    @Log(title = "用户", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(accountService.deleteAccountByIds(ids));
    }
}
