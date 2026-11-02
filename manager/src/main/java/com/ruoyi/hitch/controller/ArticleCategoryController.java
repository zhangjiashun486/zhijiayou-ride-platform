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
import com.ruoyi.hitch.domain.ArticleCategory;
import com.ruoyi.hitch.service.IArticleCategoryService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 文章分类Controller
 * 
 * @author Shawn
 * @date 2026-08-13
 */
@Controller
@RequestMapping("/hitch/category")
public class ArticleCategoryController extends BaseController
{
    private String prefix = "hitch/category";

    @Autowired
    private IArticleCategoryService articleCategoryService;

    @RequiresPermissions("hitch:category:view")
    @GetMapping()
    public String category()
    {
        return prefix + "/category";
    }

    /**
     * 查询文章分类列表
     */
    @RequiresPermissions("hitch:category:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(ArticleCategory articleCategory)
    {
        startPage();
        List<ArticleCategory> list = articleCategoryService.selectArticleCategoryList(articleCategory);
        return getDataTable(list);
    }

    /**
     * 导出文章分类列表
     */
    @RequiresPermissions("hitch:category:export")
    @Log(title = "文章分类", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(ArticleCategory articleCategory)
    {
        List<ArticleCategory> list = articleCategoryService.selectArticleCategoryList(articleCategory);
        ExcelUtil<ArticleCategory> util = new ExcelUtil<ArticleCategory>(ArticleCategory.class);
        return util.exportExcel(list, "文章分类数据");
    }

    /**
     * 新增文章分类
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存文章分类
     */
    @RequiresPermissions("hitch:category:add")
    @Log(title = "文章分类", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(ArticleCategory articleCategory)
    {
        return toAjax(articleCategoryService.insertArticleCategory(articleCategory));
    }

    /**
     * 修改文章分类
     */
    @RequiresPermissions("hitch:category:edit")
    @GetMapping("/edit/{categoryId}")
    public String edit(@PathVariable("categoryId") Long categoryId, ModelMap mmap)
    {
        ArticleCategory articleCategory = articleCategoryService.selectArticleCategoryByCategoryId(categoryId);
        mmap.put("articleCategory", articleCategory);
        return prefix + "/edit";
    }

    /**
     * 修改保存文章分类
     */
    @RequiresPermissions("hitch:category:edit")
    @Log(title = "文章分类", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(ArticleCategory articleCategory)
    {
        return toAjax(articleCategoryService.updateArticleCategory(articleCategory));
    }

    /**
     * 删除文章分类
     */
    @RequiresPermissions("hitch:category:remove")
    @Log(title = "文章分类", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(articleCategoryService.deleteArticleCategoryByCategoryIds(ids));
    }
}
