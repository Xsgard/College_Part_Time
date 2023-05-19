package com.cqucc.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cqucc.common.R;
import com.cqucc.dto.JobDto;
import com.cqucc.entity.Job;
import com.cqucc.entity.User;
import com.cqucc.entity.UserMessage;
import com.cqucc.service.CategoryService;
import com.cqucc.service.JobService;
import com.cqucc.service.UserMessageService;
import com.cqucc.service.UserService;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private JobService jobService;

    @Autowired
    private UserMessageService userMessageService;

    /**
     * 用户登录
     *
     * @param request
     * @param user
     * @return
     */
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
            return R.error("您未注册账号，请先注册！");
        }
        //4.密码比对，结果不一致返回登录失败
        if (!one.getPassword().equals(password)) {
            return R.error("密码错误，登陆失败！");
        }
        //5.查看账户是否通过管理员审核
        if (one.getStatus() != 1) {
            if (one.getStatus() == 0) {
                request.getSession().setAttribute("user", one.getId());
                return R.success(one);
            } else if (one.getStatus() == 2) {
                return R.error("审核未通过，请重新注册或联系管理员！");
            }
        }
        //6.登录成功，将员工id放入session中，并返回登录成功结果
        request.getSession().setAttribute("user", one.getId());
        return R.success(one);
    }

    /**
     * 用户退出
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("user");
        return R.success("登出成功！");
    }

    /**
     * 用户注册
     *
     * @param user
     * @return
     */
    @PostMapping("/register")
    public R<String> register(@RequestBody User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(user.getUsername()), User::getUsername, user.getUsername());
        User one = userService.getOne(queryWrapper);
        if (one != null)
            return R.error("您输入的账户Id已被抢注，请重新注册！");
        if (!"".equals(user.getPassword()) && user.getPassword() != null) {
            user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8)));
        } else {
            user.setPassword(DigestUtils.md5DigestAsHex(User.DEFAULT_PASSWORD.getBytes(StandardCharsets.UTF_8)));
        }
        user.setImage("d562e08f-e0fd-49be-a5b8-0b66948dfecc.jpeg");

        if (user.getLicense() != null && !"".equals(user.getLicense())) {
            user.setIdentity(2);
        } else {
            user.setIdentity(1);
        }
        if (user.getIdentity() == 1) {
            user.setStatus(1);
        }
        userService.save(user);
        return R.success("注册成功！");
    }

    /**
     * 根据ID获取用户信息
     *
     * @param id
     * @return
     */
    @GetMapping("/list/{id}")
    public R<Page<User>> getUserList(@PathVariable Long id) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId, id);
        User user = userService.getOne(queryWrapper);
        Page<User> p = new Page<>(1, 1);
        List<User> userList = new ArrayList<>();
        userList.add(user);
        p.setRecords(userList);

        return R.success(p);
    }


    /**
     * 修改用户信息
     *
     * @param user
     * @return
     */
    @PutMapping
    @Transactional
    public R<String> update(@RequestBody User user, HttpSession session) {
        Long userId = (Long) session.getAttribute("user");
        //通过session中存储的userId查询数据库中原User实体数据
        User originUser = userService.getById(userId);
        if (!Objects.equals(originUser.getUsername(), user.getUsername())) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUsername, user.getUsername());
            int count = userService.count(queryWrapper);
            if (count > 0) {
                return R.error("此用户ID已被注册，请您重新填写！");
            }
        }
        if (user.getPassword() == null || "".equals(user.getPassword())) {
            user.setPassword(DigestUtils.md5DigestAsHex(User.DEFAULT_PASSWORD.getBytes(StandardCharsets.UTF_8)));
        } else {
            user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8)));
        }
        if (!originUser.getLicense().equals(user.getLicense())) {
            user.setStatus(0);
            userService.updateById(user);
            return R.success("您的营业执照已重新上传，请等待管理员审核！");
        }
        userService.updateById(user);
        return R.success("修改成功！");
    }

    @PutMapping("/status")
    public R<String> updateStatus(@RequestBody User user) {
        userService.updateById(user);
        return R.success("修改成功！");
    }

    /**
     * 审核页面信息分页查询
     *
     * @param e
     * @return
     */
    @PostMapping("/page")
    public R<Page> getPage(@RequestBody examine e) {
        Integer page = e.getPage();
        Integer pageSize = e.getPageSize();
        String name = e.getName();
        Integer type = e.getValue();
        if (type == 1) {
            Page<User> userPage = new Page<>(page, pageSize);
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getStatus, 0);
            queryWrapper.like(StringUtils.isNotEmpty(name), User::getUsername, name);
            userService.page(userPage, queryWrapper);

            return R.success(userPage);
        } else if (type == 2) {
            Page<Job> jobPage = new Page<>(page, pageSize);
            Page<JobDto> dtoPage = new Page<>();
            LambdaQueryWrapper<Job> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Job::getStatus, 0);
            queryWrapper.like(StringUtils.isNotEmpty(name), Job::getJobName, name);
            jobService.page(jobPage, queryWrapper);
            BeanUtils.copyProperties(jobPage, dtoPage, "records");
            List<Job> records = jobPage.getRecords();
            List<JobDto> list = records.stream().map((item) -> {
                JobDto jobDto = new JobDto();
                BeanUtils.copyProperties(item, jobDto);
                Long categoryId = item.getCategoryId();
                if (categoryId != null) {
                    String s = categoryService.getById(categoryId).getName();
                    jobDto.setCategoryName(s);
                }
                return jobDto;
            }).collect(Collectors.toList());
            dtoPage.setRecords(list);

            return R.success(dtoPage);
        }

        return R.error("报错了，我也不知道为什么...");
    }

    @GetMapping("/page")
    public R<Page<User>> getUserList(Integer page, Integer pageSize, String name) {
        //分页构造器
        Page<User> pageInfo = new Page<>(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), User::getName, name);
        userService.page(pageInfo, queryWrapper);
        List<User> records = pageInfo.getRecords();
        for (int i = 0; i < records.size(); i++) {
            User user = records.get(i);
            if ("admin".equals(user.getUsername())) {
                records.remove(i);
                break;
            }
        }
        return R.success(pageInfo);
    }

    @GetMapping("/passCom/{ids}")
    public R<String> passCom(@PathVariable Long[] ids) {
        for (Long id :
                ids) {
            User user = userService.getById(id);
            user.setStatus(1);
            userService.updateById(user);
            return R.success("审核已通过！");
        }
        return R.error("发生错误！");
    }

    @GetMapping("/passJob/{ids}")
    public R<String> passJob(@PathVariable Long[] ids) {
        for (Long id :
                ids) {
            Job job = jobService.getById(id);
            job.setStatus(1);
            jobService.updateById(job);
            return R.success("审核已通过！");
        }
        return R.error("发生错误！");
    }

    @DeleteMapping("/unPass")
    public R<String> unPassCom(@RequestBody Long id) {
        boolean b = userService.removeById(id);
        if (!b) {
            return R.error("操作失败！");
        } else {
            return R.success("操作成功!");
        }
    }

    @GetMapping("/getUsername")
    public R<String> getUsername(HttpSession session) {
        Long userId = (Long) session.getAttribute("user");
        User user = userService.getById(userId);
        return R.success(user.getName());
    }

    @GetMapping("/historyShow")
    public R<List<UserMessage>> historyShow(HttpSession session) {
        Long userId = (Long) session.getAttribute("user");
        String username = userService.getById(userId).getName();
        //新建list集合存储数据
        LambdaQueryWrapper<UserMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserMessage::getUsername, username)
                .or().eq(UserMessage::getToname, username);
        List<UserMessage> historyList = userMessageService.list(queryWrapper);
        return R.success(historyList);
    }

    @Data
    private static class examine {
        private Integer page;
        private Integer pageSize;
        private String name;
        private Integer value;
    }
}
