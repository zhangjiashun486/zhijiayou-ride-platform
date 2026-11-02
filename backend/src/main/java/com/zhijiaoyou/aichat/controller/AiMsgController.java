package com.zhijiaoyou.aichat.controller;

import com.zhijiaoyou.aichat.entity.po.AiMsg;
import com.zhijiaoyou.aichat.service.IAiMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 资讯库 接口
 */
@RestController
@RequestMapping("/ai-msg")
public class AiMsgController {

    @Autowired
    private IAiMsgService aiMsgService;

    @GetMapping("/{id}")
    public AiMsg getById(@PathVariable Integer id) {
        return aiMsgService.getById(id);
    }

    @PostMapping("/list")
    public List<AiMsg> list(@RequestBody AiMsg condition) {
        return aiMsgService.list(condition);
    }

    @PostMapping
    public boolean save(@RequestBody AiMsg record) {
        return aiMsgService.save(record);
    }

    @PutMapping
    public boolean update(@RequestBody AiMsg record) {
        return aiMsgService.updateById(record);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        return aiMsgService.removeById(id);
    }
}
