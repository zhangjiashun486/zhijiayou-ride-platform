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
import com.ruoyi.hitch.domain.AiFiles;
import com.ruoyi.hitch.service.IAiFilesService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 文件库Controller
 * 
 * @author Shawn
 * @date 2026-08-21
 */
@Controller
@RequestMapping("/hitch/files")
public class AiFilesController extends BaseController
{
    private String prefix = "hitch/files";

    @Autowired
    private IAiFilesService aiFilesService;

    @RequiresPermissions("hitch:files:view")
    @GetMapping()
    public String files()
    {
        return prefix + "/files";
    }

    /**
     * 查询文件库列表
     */
    @RequiresPermissions("hitch:files:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(AiFiles aiFiles)
    {
        startPage();
        List<AiFiles> list = aiFilesService.selectAiFilesList(aiFiles);
        return getDataTable(list);
    }

    /**
     * 导出文件库列表
     */
    @RequiresPermissions("hitch:files:export")
    @Log(title = "文件库", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(AiFiles aiFiles)
    {
        List<AiFiles> list = aiFilesService.selectAiFilesList(aiFiles);
        ExcelUtil<AiFiles> util = new ExcelUtil<AiFiles>(AiFiles.class);
        return util.exportExcel(list, "文件库数据");
    }

    /**
     * 新增文件库
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存文件库
     */
    @RequiresPermissions("hitch:files:add")
    @Log(title = "文件库", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(AiFiles aiFiles)
    {
        int rows=aiFilesService.insertAiFiles(aiFiles);
        if(rows>0) {
            //新增文件 ->通知backend 解析 并向量化
            rabbitSendService.sendAddFile(aiFiles.getId()+"");
        }
        return toAjax(rows);
    }

    /**
     * 修改文件库
     */
    @RequiresPermissions("hitch:files:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        AiFiles aiFiles = aiFilesService.selectAiFilesById(id);
        mmap.put("aiFiles", aiFiles);
        return prefix + "/edit";
    }

    /**
     * 修改保存文件库
     */
    @RequiresPermissions("hitch:files:edit")
    @Log(title = "文件库", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(AiFiles aiFiles)
    {
        int rows=aiFilesService.updateAiFiles(aiFiles);
        if(rows>0) {
            //修改文件 ->通知backend 更新向量
            rabbitSendService.sendUpdateFile(aiFiles.getId()+"");
        }
        return toAjax(rows);
    }

    /**
     * 删除文件库
     */
    @RequiresPermissions("hitch:files:remove")
    @Log(title = "文件库", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        int rows=aiFilesService.deleteAiFilesByIds(ids);
        if(rows>0) {
            //删除文件 ->通知backend 删除文件
            rabbitSendService.sendDeleteFile(ids);
        }
        return toAjax(rows);
    }
}
