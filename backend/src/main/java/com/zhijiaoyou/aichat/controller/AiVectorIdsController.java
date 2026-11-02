package com.zhijiaoyou.aichat.controller;

import com.zhijiaoyou.aichat.entity.po.AiVectorIds;
import com.zhijiaoyou.aichat.service.IAiVectorIdsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 向量ID映射 接口
 */
@RestController
@RequestMapping("/ai-vector-ids")
public class AiVectorIdsController {

    @Autowired
    private IAiVectorIdsService service;

    @GetMapping("/query")
    public AiVectorIds getByTypeAndSource(@RequestParam String type, @RequestParam String sourceId) {
        return service.getByTypeAndSourceId(type, sourceId);
    }

    @GetMapping("/type/{type}")
    public List<AiVectorIds> listByType(@PathVariable String type) {
        return service.listByType(type);
    }

    @PostMapping
    public boolean save(@RequestBody AiVectorIds record) {
        return service.save(record);
    }

    @DeleteMapping("/doc/{documentId}")
    public boolean deleteByDocId(@PathVariable String documentId) {
        return service.removeByDocumentId(documentId);
    }
}
