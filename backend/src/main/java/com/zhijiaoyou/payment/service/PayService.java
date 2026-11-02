package com.zhijiaoyou.payment.service;

import com.zhijiaoyou.modules.bo.PayResultBO;
import com.zhijiaoyou.modules.po.OrderPO;

public interface PayService {
    /**
     * 预支付接口
     * @param orderPO
     * @return
     * @throws Exception
     */
    public PayResultBO prePay(OrderPO orderPO) throws Exception;

    /**
     * 订单查询
     * @param orderId
     * @return
     */
    public PayResultBO orderQuery(String orderId) throws Exception;


}
