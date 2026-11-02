package com.zhijiaoyou.aichat.entity.po;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 资讯库
 */
public class AiMsg implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer category;
    private String title;
    private String pic;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String dictLabel;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getCategory() { return category; }
    public void setCategory(Integer category) { this.category = category; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getPic() { return pic; }
    public void setPic(String pic) { this.pic = pic; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    public String getDictLabel() { return dictLabel; }
    public void setDictLabel(String dictLabel) { this.dictLabel = dictLabel; }
}
