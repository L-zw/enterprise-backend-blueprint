package com.lzw.blueprint.admin.controller;

import com.lzw.blueprint.admin.dto.LoginRequest;
import com.lzw.blueprint.admin.entity.SysUser;
import com.lzw.blueprint.admin.service.SysMenuService;
import com.lzw.blueprint.admin.service.SysUserService;
import com.lzw.blueprint.admin.vo.LoginResponse;
import com.lzw.blueprint.admin.vo.MenuVO;
import com.lzw.blueprint.common.Result;
import com.lzw.blueprint.common.ResultCode;
import com.lzw.blueprint.core.util.JwtUtil;
import com.lzw.blueprint.core.util.PasswordEncoderUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

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
    private SysMenuService sysMenuService;

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

    @Operation(summary = "当前用户路由树")
    @GetMapping("/routers")
    public Result<List<MenuVO>> routers(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return success(sysMenuService.getRouterTree(userId));
    }

    @Operation(summary = "当前用户权限标识")
    @GetMapping("/permissions")
    public Result<Set<String>> permissions(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return success(sysMenuService.getPermissionSet(userId));
    }
}