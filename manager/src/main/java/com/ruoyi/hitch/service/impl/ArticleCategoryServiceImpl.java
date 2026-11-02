package com.ruoyi.hitch.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.hitch.domain.Article;
import com.ruoyi.hitch.mapper.ArticleCategoryMapper;
import com.ruoyi.hitch.domain.ArticleCategory;
import com.ruoyi.hitch.service.IArticleCategoryService;
import com.ruoyi.common.core.text.Convert;

/**
 * 文章分类Service业务层处理
 * 
 * @author Shawn
 * @date 2026-08-13
 */
@Service
public class ArticleCategoryServiceImpl implements IArticleCategoryService 
{
    @Autowired
    private ArticleCategoryMapper articleCategoryMapper;

    /**
     * 查询文章分类
     * 
     * @param categoryId 文章分类主键
     * @return 文章分类
     */
    @Override
    public ArticleCategory selectArticleCategoryByCategoryId(Long categoryId)
    {
        return articleCategoryMapper.selectArticleCategoryByCategoryId(categoryId);
    }

    /**
     * 查询文章分类列表
     * 
     * @param articleCategory 文章分类
     * @return 文章分类
     */
    @Override
    public List<ArticleCategory> selectArticleCategoryList(ArticleCategory articleCategory)
    {
        return articleCategoryMapper.selectArticleCategoryList(articleCategory);
    }

    /**
     * 新增文章分类
     * 
     * @param articleCategory 文章分类
     * @return 结果
     */
    @Transactional
    @Override
    public int insertArticleCategory(ArticleCategory articleCategory)
    {
        int rows = articleCategoryMapper.insertArticleCategory(articleCategory);
        insertArticle(articleCategory);
        return rows;
    }

    /**
     * 修改文章分类
     * 
     * @param articleCategory 文章分类
     * @return 结果
     */
    @Transactional
    @Override
    public int updateArticleCategory(ArticleCategory articleCategory)
    {
        articleCategoryMapper.deleteArticleByCategoryId(articleCategory.getCategoryId());
        insertArticle(articleCategory);
        return articleCategoryMapper.updateArticleCategory(articleCategory);
    }

    /**
     * 批量删除文章分类
     * 
     * @param categoryIds 需要删除的文章分类主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteArticleCategoryByCategoryIds(String categoryIds)
    {
        articleCategoryMapper.deleteArticleByCategoryIds(Convert.toStrArray(categoryIds));
        return articleCategoryMapper.deleteArticleCategoryByCategoryIds(Convert.toStrArray(categoryIds));
    }

    /**
     * 删除文章分类信息
     * 
     * @param categoryId 文章分类主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteArticleCategoryByCategoryId(Long categoryId)
    {
        articleCategoryMapper.deleteArticleByCategoryId(categoryId);
        return articleCategoryMapper.deleteArticleCategoryByCategoryId(categoryId);
    }

    /**
     * 新增文章信息
     * 
     * @param articleCategory 文章分类对象
     */
    public void insertArticle(ArticleCategory articleCategory)
    {
        List<Article> articleList = articleCategory.getArticleList();
        Long categoryId = articleCategory.getCategoryId();
        if (StringUtils.isNotNull(articleList))
        {
            List<Article> list = new ArrayList<Article>();
            for (Article article : articleList)
            {
                article.setCategoryId(categoryId);
                list.add(article);
            }
            if (list.size() > 0)
            {
                articleCategoryMapper.batchArticle(list);
            }
        }
    }
}
