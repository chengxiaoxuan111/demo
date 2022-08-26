package com.demo.controller;

import com.demo.service.IUserService;
import org.checkerframework.checker.units.qual.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author 程啸轩
 * @date 2022/8/26 16:26
 */
@RestController
@RequestMapping("/user")
@Validated
public class UserController {
    @Autowired
    IUserService userService;

    @PostMapping("/register")
    public CommonResult<String> registry(@Valid @RequestBody UserDTO userDTO) {
        userService.registry(userDTO);
        return ResultUtils.success();
    }

    @PostMapping("/login")
    public CommonResult<String> login(@NotBlank(message = "用户名不能为空") @RequestParam String username,
                                      @Length(min = 6, max = 255, message = "密码长度不能小于6位") @RequestParam String password) {
        return ResultUtils.success(userService.login(username, password));
    }

    @GetMapping("/test")
    @PreAuthorize("hasAuthority('admin:test')")
    public CommonResult<String> test() {
        return ResultUtils.success("test");
    }
}

