package com.zhijiaoyou.commons.utils;

import com.zhijiaoyou.commons.constant.HtichConstants;
import com.zhijiaoyou.modules.po.AccountPO;
import com.zhijiaoyou.modules.vo.AccountVO;
import com.zhijiaoyou.storage.service.AccountAPIService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class RequestUtils {

    /**
     * 获取Request对象
     *
     * @return
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes.getRequest();
    }

    /**
     * 获取当前用户信息
     *
     * @return
     */
    public static String getCurrentUserId() {
        HttpServletRequest request = getRequest();
        AccountVO vo = (AccountVO) request.getSession().getAttribute("user");
        if (vo == null) return null;
        return vo.getId();
    }

    public static void refreshSessionUser(){
        String userId = getCurrentUserId();
        if (userId == null){
            return;
        }
        AccountAPIService accountAPIService = SpringUtil.getBean(AccountAPIService.class);
        AccountPO po = accountAPIService.getAccountByID(userId);
        getRequest().getSession().setAttribute("user",CommonsUtils.toVO(po));
    }


}
