package com.lzw.blueprint.admin.controller;

import com.lzw.blueprint.admin.dto.LoginRequest;
import com.lzw.blueprint.admin.entity.SysUser;
import com.lzw.blueprint.admin.service.SysUserService;
import com.lzw.blueprint.admin.vo.LoginResponse;
import com.lzw.blueprint.common.Result;
import com.lzw.blueprint.common.ResultCode;
import com.lzw.blueprint.core.util.JwtUtil;
import com.lzw.blueprint.core.util.PasswordEncoderUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口
 */
@Tag(name = "认证管理", description = "登录 / 登出接口")
@RestController
@RequestMapping("/auth")
public class AuthController extends BaseController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoderUtil passwordEncoderUtil;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        SysUser user = sysUserService.findByUsername(request.getUsername());
        if (user == null || !passwordEncoderUtil.matches(request.getPassword(), user.getPassword())) {
            return Result.fail(ResultCode.UNAUTHORIZED.getCode(), "用户名或密码错误");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        LoginResponse resp = new LoginResponse();
        resp.setToken(token);
        resp.setUsername(user.getUsername());
        return success(resp);
    }
}