package com.ruoyi.hitch.service;

import java.util.List;
import com.ruoyi.hitch.domain.ArticleCategory;

/**
 * 文章分类Service接口
 * 
 * @author Shawn
 * @date 2026-08-13
 */
public interface IArticleCategoryService 
{
    /**
     * 查询文章分类
     * 
     * @param categoryId 文章分类主键
     * @return 文章分类
     */
    public ArticleCategory selectArticleCategoryByCategoryId(Long categoryId);

    /**
     * 查询文章分类列表
     * 
     * @param articleCategory 文章分类
     * @return 文章分类集合
     */
    public List<ArticleCategory> selectArticleCategoryList(ArticleCategory articleCategory);

    /**
     * 新增文章分类
     * 
     * @param articleCategory 文章分类
     * @return 结果
     */
    public int insertArticleCategory(ArticleCategory articleCategory);

    /**
     * 修改文章分类
     * 
     * @param articleCategory 文章分类
     * @return 结果
     */
    public int updateArticleCategory(ArticleCategory articleCategory);

    /**
     * 批量删除文章分类
     * 
     * @param categoryIds 需要删除的文章分类主键集合
     * @return 结果
     */
    public int deleteArticleCategoryByCategoryIds(String categoryIds);

    /**
     * 删除文章分类信息
     * 
     * @param categoryId 文章分类主键
     * @return 结果
     */
    public int deleteArticleCategoryByCategoryId(Long categoryId);
}
