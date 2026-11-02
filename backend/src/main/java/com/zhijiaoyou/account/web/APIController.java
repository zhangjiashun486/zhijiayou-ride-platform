package com.zhijiaoyou.account.web;

import com.zhijiaoyou.account.handler.AccountHandler;
import com.zhijiaoyou.commons.constant.HtichConstants;
import com.zhijiaoyou.commons.domin.vo.response.ResponseVO;
import com.zhijiaoyou.commons.enums.BusinessErrors;
import com.zhijiaoyou.commons.exception.BusinessRuntimeException;
import com.zhijiaoyou.commons.groups.Group;
import com.zhijiaoyou.commons.initial.annotation.RequestInitial;
import com.zhijiaoyou.commons.utils.CommonsUtils;
import com.zhijiaoyou.modules.vo.AccountVO;
import com.zhijiaoyou.modules.vo.AuthenticationVO;
import com.zhijiaoyou.modules.vo.VehicleVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;


@RestController("account")
@RequestMapping("/account/api/")
@Tag(name = "账户操作Controller")
@ApiResponses(@ApiResponse(responseCode = "200", description = "处理成功"))
public class APIController {

    @Autowired
    private AccountHandler accountHandler;

    private final static Logger logger = LoggerFactory.getLogger(APIController.class);

    @Operation(summary = "用户注册接口")
    @PostMapping("/register")
    @RequestInitial(groups = {Group.Create.class})
    public ResponseVO<AccountVO> register(@Validated(Group.Create.class) @RequestBody AccountVO accountVO) {
        return accountHandler.register(accountVO);
    }

    @Operation(summary = "用户登录接口")
    @PostMapping("/login")
    public ResponseVO<AccountVO> login(@Validated(Group.Select.class) @RequestBody AccountVO accountVO, HttpServletRequest request) {
        AccountVO vo = accountHandler.verifyAccountLogin(accountVO);
        vo.setToken(vo.getId());
        request.getSession().setAttribute("user",vo);
        return ResponseVO.success(vo);
    }

    @Operation(summary = "修改密码接口")
    @PostMapping("/modifyPassword")
    @RequestInitial
    public ResponseVO<AccountVO> modifyPassword(@RequestBody AccountVO accountVO) {
        return accountHandler.modifyPassword(accountVO);
    }


    @Operation(summary = "修改用户信息接口")
    @PostMapping("/modify")
    @RequestInitial
    public ResponseVO<AccountVO> modify(@RequestBody AccountVO accountVO) {
        return accountHandler.modify(accountVO);
    }


    @Operation(summary = "获取用户基本信息")
    @PostMapping("/userinfo")
    @RequestInitial
    public ResponseVO<AccountVO> userinfo() {
        return accountHandler.userinfo();
    }

    @Operation(summary = "获取用户认证信息")
    @PostMapping("/getAuthenticationInfo")
    @RequestInitial(groups = {Group.Select.class})
    public ResponseVO<AuthenticationVO> getAuthenticationInfo() {
        return accountHandler.getAuthenticationInfo();
    }

    @Operation(summary = "用户资料修改接口")
    @PostMapping("/modifyAuthentication")
    public ResponseVO<AuthenticationVO> modifyAuthentication(@RequestBody AuthenticationVO authenticationVO) {
        return accountHandler.modifyAuthentication(authenticationVO);
    }

    @Operation(summary = "获取车辆认证信息")
    @PostMapping("/getVehicleInfo")
    public ResponseVO<VehicleVO> getVehicleInfo() {
        return accountHandler.getVehicleInfo();
    }

    @Operation(summary = "车辆资料修改接口")
    @PostMapping("/modifyVehicle")
    public ResponseVO<VehicleVO> modifyVehicle(@RequestBody VehicleVO vehicleVO) {
        return accountHandler.modifyVehicle(vehicleVO);
    }

    @Operation(summary = "验证Token")
    @PostMapping("/verifyToken")
    public ResponseVO verifyToken(HttpServletRequest request) {
        //主动在个人中心退出登录会清空客户端token，拿到null
        String token = request.getHeader(HtichConstants.SESSION_TOKEN_KEY);
        if (token == null || "null".equals(token)){
            //同步清理掉redis session
            request.getSession().invalidate();
            throw new BusinessRuntimeException(BusinessErrors.DATA_NOT_EXIST);
        }
        AccountVO vo = (AccountVO) request.getSession().getAttribute("user");
        if (vo == null){
            throw new BusinessRuntimeException(BusinessErrors.DATA_STATUS_ERROR);
        } else if (!vo.getId().equals(token)) {
            throw new BusinessRuntimeException(BusinessErrors.AUTHENTICATION_ERROR);
        }
        return ResponseVO.success(CommonsUtils.toPO(vo));
    }

    @Operation(summary = "身份认证接口")
    @PostMapping("/identityAuth")
    public ResponseVO<AuthenticationVO> identityAuth() {
        return accountHandler.identityAuth();
    }

    @Operation(summary = "车辆认证接口")
    @PostMapping("/vehicleAuth")
    public ResponseVO<VehicleVO> vehicleAuth() {
        return accountHandler.vehicleAuth();
    }


}
