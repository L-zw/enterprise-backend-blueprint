package com.lzw.blueprint.admin.controller;

import com.lzw.blueprint.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统测试接口
 * 验证项目启动、统一返回格式、全局异常处理是否正常工作
 */
@Tag(name = "测试", description = "系统测试接口")
@Validated
@RestController
public class HelloController extends BaseController {

    @Operation(summary = "Hello 测试")
    @GetMapping("/hello")
    public Result<String> hello() {
        return success("Hello Blueprint");
    }

    @Operation(summary = "参数校验测试")
    @GetMapping("/hello/validate")
    public Result<String> validate(@RequestParam @NotBlank(message = "名称不能为空") String name) {
        return success("Hello " + name);
    }
}
