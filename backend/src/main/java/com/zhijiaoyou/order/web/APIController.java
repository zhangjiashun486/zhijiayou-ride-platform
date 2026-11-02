package com.zhijiaoyou.order.web;


import com.zhijiaoyou.commons.domin.vo.response.ResponseVO;
import com.zhijiaoyou.commons.groups.Group;
import com.zhijiaoyou.commons.initial.annotation.RequestInitial;
import com.zhijiaoyou.modules.vo.OrderVO;
import com.zhijiaoyou.order.handler.OrderHandler;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("order")
@RequestMapping("/order/api/")
@Tag(name = "订单操作Controller")
@ApiResponses(@ApiResponse(responseCode = "200", description = "处理成功"))
public class APIController {
    @Autowired
    private OrderHandler orderHandler;


    @Operation(summary = "订单列表")
    @PostMapping("/list")
    @RequestInitial(groups = {Group.Select.class})
    public ResponseVO<OrderVO> list(@RequestBody OrderVO orderVO) {
        return orderHandler.list(orderVO);
    }

    @Operation(summary = "生成订单影子")
    @PostMapping("/generateShadow")
    public ResponseVO<OrderVO> generateShadow() {
        return orderHandler.generateShadow();
    }


    @Operation(summary = "已支付订单列表")
    @PostMapping("/paidList")
    public ResponseVO<OrderVO> paidList() {
        return orderHandler.paidList();
    }


    @Operation(summary = "同行乘客列表")
    @PostMapping("/fellows")
    @RequestInitial(groups = {Group.Select.class})
    public ResponseVO<OrderVO> fellows(@RequestBody OrderVO orderVO) {
        return orderHandler.fellows(orderVO);
    }

    @Operation(summary = "根据乘客查看订单信息")
    @PostMapping("/view/order/{tripid}")
    public ResponseVO<OrderVO> viewOrder(@PathVariable("tripid") String tripid) {
        return orderHandler.viewOrder(tripid);
    }


}
