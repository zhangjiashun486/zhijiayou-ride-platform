package com.ruoyi.hitch.mapper;

import java.util.List;
import com.ruoyi.hitch.domain.ArticleCategory;
import com.ruoyi.hitch.domain.Article;

/**
 * 文章分类Mapper接口
 * 
 * @author Shawn
 * @date 2026-08-13
 */
public interface ArticleCategoryMapper 
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
     * 删除文章分类
     * 
     * @param categoryId 文章分类主键
     * @return 结果
     */
    public int deleteArticleCategoryByCategoryId(Long categoryId);

    /**
     * 批量删除文章分类
     * 
     * @param categoryIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteArticleCategoryByCategoryIds(String[] categoryIds);

    /**
     * 批量删除文章
     * 
     * @param categoryIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteArticleByCategoryIds(String[] categoryIds);
    
    /**
     * 批量新增文章
     * 
     * @param articleList 文章列表
     * @return 结果
     */
    public int batchArticle(List<Article> articleList);
    

    /**
     * 通过文章分类主键删除文章信息
     * 
     * @param categoryId 文章分类ID
     * @return 结果
     */
    public int deleteArticleByCategoryId(Long categoryId);
}
