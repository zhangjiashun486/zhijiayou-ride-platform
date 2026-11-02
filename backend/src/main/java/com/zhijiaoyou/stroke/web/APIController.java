package com.zhijiaoyou.stroke.web;

import com.zhijiaoyou.commons.domin.bo.WorldMapBO;
import com.zhijiaoyou.commons.domin.vo.response.ResponseVO;
import com.zhijiaoyou.commons.groups.Group;
import com.zhijiaoyou.commons.initial.annotation.RequestInitial;
import com.zhijiaoyou.modules.vo.StrokeVO;
import com.zhijiaoyou.stroke.handler.StrokeHandler;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController("stroke")
@RequestMapping("/stroke/api/")
@Tag(name = "行程操作Controller")
@ApiResponses(@ApiResponse(responseCode = "200", description = "处理成功"))
public class APIController {

    @Autowired
    private StrokeHandler strokeHandler;


    @Operation(summary = "发布行程")
    @PostMapping("/publish")
    @RequestInitial(groups = {Group.Create.class})
    public ResponseVO<StrokeVO> publish(@Validated(Group.Create.class) @RequestBody StrokeVO strokeVO) {
        return strokeHandler.publish(strokeVO);
    }

    @Operation(summary = "发送起始坐标")
    @PostMapping("/sendStartGeo")
    public ResponseVO<StrokeVO> sendStartGeo(@RequestBody WorldMapBO worldMapBO) {
        strokeHandler.sendStartGeo(worldMapBO);
        return ResponseVO.success(null);
    }

    @Operation(summary = "修改行程信息")
    @PostMapping("/update")
    @RequestInitial(groups = {Group.Update.class})
    public ResponseVO<StrokeVO> update(@Validated(Group.Update.class) @RequestBody StrokeVO strokeVO) {
        return strokeHandler.update(strokeVO);
    }


    @Operation(summary = "查看个人行程列表")
    @PostMapping("/list")
    @RequestInitial(groups = Group.Select.class)
    public ResponseVO<StrokeVO> list(@RequestBody StrokeVO strokeVO) {
        return strokeHandler.list(strokeVO);
    }

    @Operation(summary = "查看行程细节")
    @PostMapping("/detail/{id}")
    @RequestInitial(groups = Group.Select.class)
    public ResponseVO<StrokeVO> detail(@PathVariable("id") String id) {
        return strokeHandler.detail(id);
    }

    @Operation(summary = "查看顺路行程列表")
    @PostMapping("/itinerary/list")
    @RequestInitial(groups = Group.Select.class)
    public ResponseVO<StrokeVO> itineraryList(@RequestBody StrokeVO strokeVO) {
        return strokeHandler.itineraryList(strokeVO);
    }

    @Operation(summary = "顺风车邀请接口")
    @PostMapping("/invite")
    @RequestInitial(groups = Group.Select.class)
    public ResponseVO<StrokeVO> invite(@RequestBody StrokeVO strokeVO) {
        return strokeHandler.invite(strokeVO);
    }

    @Operation(summary = "顺风车邀请列表")
    @PostMapping("/invite/list/{tripid}")
    @RequestInitial(groups = Group.Select.class)
    public ResponseVO<StrokeVO> inviteList(@PathVariable("tripid") String tripid) {
        return strokeHandler.inviteList(tripid);
    }

    @Operation(summary = "顺风车接受邀请")
    @PostMapping("/invite/accept")
    public ResponseVO<StrokeVO> inviteAccept(@RequestBody StrokeVO strokeVO) {
        return strokeHandler.inviteAccept(strokeVO);
    }


    @Operation(summary = "乘客上车")
    @PostMapping("/hitchhiker/{tripid}")
    public ResponseVO<StrokeVO> hitchhiker(@PathVariable("tripid") String tripid) {
        return strokeHandler.hitchhiker(tripid);
    }

    @Operation(summary = "乘客下车")
    @PostMapping("/freeride/{tripid}")
    public ResponseVO<StrokeVO> freeride(@PathVariable("tripid") String tripid) {
        return strokeHandler.freeride(tripid);
    }

    @Operation(summary = "司机发车")
    @PostMapping("/departure/{tripid}")
    public ResponseVO<StrokeVO> departure(@PathVariable("tripid") String tripid) {
        return strokeHandler.departure(tripid);
    }

    @Operation(summary = "确认送达")
    @PostMapping("/delivery/{tripid}")
    public ResponseVO<StrokeVO> delivery(@PathVariable("tripid") String tripid) {
        return strokeHandler.delivery(tripid);
    }


}
