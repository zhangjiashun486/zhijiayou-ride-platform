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
import com.ruoyi.hitch.domain.AiMsg;
import com.ruoyi.hitch.service.IAiMsgService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 资讯库Controller
 * 
 * @author Shawn
 * @date 2026-08-13
 */
@Controller
@RequestMapping("/hitch/msg")
public class AiMsgController extends BaseController
{
    private String prefix = "hitch/msg";

    @Autowired
    private IAiMsgService aiMsgService;

    @RequiresPermissions("hitch:msg:view")
    @GetMapping()
    public String msg()
    {
        return prefix + "/msg";
    }

    /**
     * 查询资讯库列表
     */
    @RequiresPermissions("hitch:msg:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(AiMsg aiMsg)
    {
        startPage();
        List<AiMsg> list = aiMsgService.selectAiMsgList(aiMsg);
        return getDataTable(list);
    }

    /**
     * 导出资讯库列表
     */
    @RequiresPermissions("hitch:msg:export")
    @Log(title = "资讯库", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(AiMsg aiMsg)
    {
        List<AiMsg> list = aiMsgService.selectAiMsgList(aiMsg);
        ExcelUtil<AiMsg> util = new ExcelUtil<AiMsg>(AiMsg.class);
        return util.exportExcel(list, "资讯库数据");
    }

    /**
     * 新增资讯库
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存资讯库
     */
    @RequiresPermissions("hitch:msg:add")
    @Log(title = "资讯库", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(AiMsg aiMsg)
    {   //新增加的修改

        //manager 里有一个 RabbitSendService，已经写好了三个方法:sendAddMsg(ids);sendUpdateMsg(ids);sendDeleteMsg(ids);
        int rows=aiMsgService.insertAiMsg(aiMsg);
        //插入成功后，发送MQ信息，告诉backend:新增了这条信息
        if(rows>0){
            rabbitSendService.sendAddMsg(String.valueOf(aiMsg.getId()));
        }
        //为什么先 insert 再 send：insertAiMsg 之后,MyBatis 会自动把数据库自增的主键回填到 aiMsg.getId() 里，这时候ID已经有了，才能发给 backend。顺序不能反。
        return toAjax(rows);
    }

    /**
     * 修改资讯库
     */
    @RequiresPermissions("hitch:msg:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        AiMsg aiMsg = aiMsgService.selectAiMsgById(id);
        mmap.put("aiMsg", aiMsg);
        return prefix + "/edit";
    }

    /**
     * 修改保存资讯库
     */
    @RequiresPermissions("hitch:msg:edit")
    @Log(title = "资讯库", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(AiMsg aiMsg)
    {
        int rows=aiMsgService.updateAiMsg(aiMsg);
        if (rows>0){
            rabbitSendService.sendUpdateMsg(aiMsg.getId()+"");//新增的是这一步：rabiitMq发送消息(数据库中的主键，也就是id)给bakend
        }
        return toAjax(rows);
    }
    //本来是return toAjax(aiMsgService.updateAiMsg(aiMsg))

    /**
     * 删除资讯库
     */
    @RequiresPermissions("hitch:msg:remove")
    @Log(title = "资讯库", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        int rows=aiMsgService.deleteAiMsgByIds(ids);
        if (rows>0){
            rabbitSendService.sendDeleteMsg(ids);//ids本身就是String
        }
        return toAjax(rows);
    }
}
