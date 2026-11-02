package com.zhijiaoyou.aichat.controller;

import com.zhijiaoyou.aichat.entity.po.AiFiles;
import com.zhijiaoyou.aichat.service.IAiFilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文件库 接口
 */
@RestController
@RequestMapping("/ai-files")
public class AiFilesController {

    @Autowired
    private IAiFilesService aiFilesService;

    @GetMapping("/{id}")
    public AiFiles getById(@PathVariable Integer id) {
        return aiFilesService.getById(id);
    }

    @GetMapping
    public List<AiFiles> listAll() {
        return aiFilesService.listAll();
    }

    @PostMapping
    public boolean save(@RequestBody AiFiles record) {
        return aiFilesService.save(record);
    }

    @PutMapping
    public boolean update(@RequestBody AiFiles record) {
        return aiFilesService.updateById(record);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        return aiFilesService.removeById(id);
    }
}
