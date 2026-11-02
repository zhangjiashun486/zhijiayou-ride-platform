package com.ruoyi.hitch.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 资讯库对象 t_ai_msg
 * 
 * @author Shawn
 * @date 2026-08-13
 */
public class AiMsg extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /**  */
    private Long id;

    /** 分类 */
    @Excel(name = "分类")
    private Long category;

    /** 标题 */
    @Excel(name = "标题")
    private String title;

    /** 图片 */
    @Excel(name = "图片")
    private String pic;

    /** 内容 */
    @Excel(name = "内容")
    private String content;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setCategory(Long category) 
    {
        this.category = category;
    }

    public Long getCategory() 
    {
        return category;
    }
    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }
    public void setPic(String pic) 
    {
        this.pic = pic;
    }

    public String getPic() 
    {
        return pic;
    }
    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("category", getCategory())
            .append("title", getTitle())
            .append("pic", getPic())
            .append("content", getContent())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
