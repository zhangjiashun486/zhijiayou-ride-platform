package com.zhijiaoyou.payment.web;

import com.zhijiaoyou.commons.domin.vo.response.ResponseVO;
import com.zhijiaoyou.commons.groups.Group;
import com.zhijiaoyou.commons.initial.annotation.RequestInitial;
import com.zhijiaoyou.commons.utils.RequestUtils;
import com.zhijiaoyou.modules.po.AccountPO;
import com.zhijiaoyou.modules.po.OrderPO;
import com.zhijiaoyou.modules.vo.OrderVO;
import com.zhijiaoyou.modules.vo.PaymentVO;
import com.zhijiaoyou.payment.handler.PaymentHandler;
import com.zhijiaoyou.storage.service.AccountAPIService;
import com.zhijiaoyou.storage.service.OrderAPIService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController("payment")
@RequestMapping("/payment/api/")
@Tag(name = "支付操作Controller")
@ApiResponses(@ApiResponse(responseCode = "200", description = "处理成功"))
public class  APIController {
    @Autowired
    private PaymentHandler paymentHandler;
    @Autowired
    private OrderAPIService orderAPIService;
    @Autowired
    private AccountAPIService accountAPIService;


    /**
     * 面对面支付
     *
     * @return
     */
    @Operation(summary = "调取支付信息")
    @PostMapping("/payment")
    @RequestInitial(groups = Group.Create.class)
    public ResponseVO<PaymentVO> payment(@RequestBody PaymentVO paymentVO) throws Exception {
        OrderPO orderPO = orderAPIService.selectByID(paymentVO.getOrderId());
        AccountPO accountPO = accountAPIService.getAccountByID(orderPO.getDriverId());
        paymentVO.setCodeUrl(accountPO.getPaycode());
        return ResponseVO.success(paymentVO);
    }

    @Operation(summary = "确认到款")
    @PostMapping("/confirmPay/{orderId}")
    @RequestInitial(groups = Group.Create.class)
    public ResponseVO<PaymentVO> confirmPay(@PathVariable String orderId) throws Exception {
        OrderPO orderPO = orderAPIService.selectByID(orderId);
        if (orderPO == null){
            return ResponseVO.error("订单不存在！");
        }
        String userId = RequestUtils.getCurrentUserId();
        //数据安全：校验是不是改的自己的订单，防止篡改数据
        if (userId!=null && !userId.equals(orderPO.getDriverId())){
            return ResponseVO.error("该订单不属于你！");
        }
        orderPO.setStatus(2);
        orderAPIService.update(orderPO);
        return ResponseVO.success("订单确认成功！");
    }



    @Operation(summary = "支付查询接口API")
    @PostMapping("/query")
    @RequestInitial(groups = Group.Select.class)
    public ResponseVO<OrderVO> orderQuery(@RequestBody PaymentVO paymentVO) throws Exception {
        OrderPO orderPO = orderAPIService.selectByID(paymentVO.getOrderId());
        return ResponseVO.success(orderPO);
    }


}
