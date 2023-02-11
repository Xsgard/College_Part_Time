package com.cqucc.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cqucc.common.R;
import com.cqucc.entity.User;
import com.cqucc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public R<User> login(HttpServletRequest request, @RequestBody User user) {
        //1.将页面提交的密码password进行md5加密
        String password = user.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.根据页面提交的username查询数据库
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, user.getUsername());
        User one = userService.getOne(queryWrapper);
        //3.如果没有查询到数据返回登录失败结果
        if (one == null) {
            return R.error("登陆失败！");
        }
        //4.密码比对，结果不一致返回登录失败
        if (!one.getPassword().equals(password)) {
            return R.error("登陆失败！");
        }
        //5.查看账户是否通过管理员审核
        if (one.getStatus() != 1) {
            return R.error("登录失败！");
        }
        //6.登录成功，将员工id放入session中，并返回登录成功结果
        request.getSession().setAttribute("user", one.getId());
        return R.success(one);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("user");
        return R.success("登出成功！");
    }

    @PostMapping("/register")
    public R<String> register(HttpServletRequest request, @RequestBody User user) {
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8)));

        if (user.getLicense() != null) {
            user.setIdentity(2);
        }
        if (user.getIdentity() == 1) {
            user.setStatus(1);
        }
        userService.save(user);
        return R.success("注册成功！");
    }
}
