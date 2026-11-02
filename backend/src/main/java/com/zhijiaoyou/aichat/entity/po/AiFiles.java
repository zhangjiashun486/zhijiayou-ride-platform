package com.zhijiaoyou.aichat.entity.po;

import java.io.Serializable;

/**
 * 文件库
 */
public class AiFiles implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private String url;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
