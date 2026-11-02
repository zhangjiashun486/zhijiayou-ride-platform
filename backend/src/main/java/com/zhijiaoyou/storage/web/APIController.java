package com.zhijiaoyou.storage.web;

import com.zhijiaoyou.commons.domin.vo.response.ResponseVO;
import com.zhijiaoyou.modules.po.AttachmentPO;
import com.zhijiaoyou.storage.handler.AttachmentHandler;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController("storage")
@RequestMapping("/storage/api/")
@Tag(name = "文件操作Controller")
@ApiResponses(@ApiResponse(responseCode = "200", description = "处理成功"))
public class APIController {


    @Autowired
    private AttachmentHandler attachmentHandler;


    @Operation(summary = "文件上传接口")
    @PostMapping("/upload")
    public ResponseVO<AttachmentPO> upload(@RequestParam("file") MultipartFile file) throws Exception {
        return attachmentHandler.uploadFile(file);
    }

    @GetMapping("/test1")
    @Cacheable(cacheNames = "cache-demo",key = "#id")
    public Object test1(@RequestParam String id){
        System.out.println("load,"+id);
        return "hello,"+id;
    }

    @GetMapping("/test2")
    @CacheEvict(cacheNames = "cache-demo",key = "#id")
    public Object test2(@RequestParam String id){
        return "hello,"+id;
    }

}
