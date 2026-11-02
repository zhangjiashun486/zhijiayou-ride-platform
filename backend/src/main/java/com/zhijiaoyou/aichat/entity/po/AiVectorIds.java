package com.zhijiaoyou.aichat.entity.po;

import java.io.Serializable;

/**
 * 向量ID映射库（业务ID → 向量库文档ID）
 */
public class AiVectorIds implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String type;
    private String sourceId;
    private String documentId;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getSourceId() { return sourceId; }
    public void setSourceId(String sourceId) { this.sourceId = sourceId; }
    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }
}
