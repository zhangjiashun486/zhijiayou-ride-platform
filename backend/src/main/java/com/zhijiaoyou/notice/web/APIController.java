package com.zhijiaoyou.notice.web;


import com.zhijiaoyou.commons.domin.vo.response.ResponseVO;
import com.zhijiaoyou.commons.groups.Group;
import com.zhijiaoyou.commons.initial.annotation.RequestInitial;
import com.zhijiaoyou.modules.vo.NoticeVO;
import com.zhijiaoyou.notice.handler.NoticeHandler;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("notice")
@RequestMapping("/notice/api/")
@Tag(name = "通知Controller")
@ApiResponses(@ApiResponse(responseCode = "200", description = "处理成功"))
public class APIController {
    @Autowired
    private NoticeHandler noticeHandler;


    @Operation(summary = "订单列表")
    @PostMapping("/list")
    @RequestInitial(groups = {Group.Select.class})
    public ResponseVO<NoticeVO> list(@RequestBody NoticeVO noticeVO) {
        return noticeHandler.list(noticeVO);
    }

}
