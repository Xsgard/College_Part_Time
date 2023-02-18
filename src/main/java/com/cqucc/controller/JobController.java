package com.cqucc.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cqucc.common.R;
import com.cqucc.entity.Job;
import com.cqucc.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.interfaces.RSAKey;

@Slf4j
@RestController
@RequestMapping("/job")
public class JobController {
    @Autowired
    private JobService jobService;

    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize, String name) {
        Page<Job> pageInfo = new Page(page, pageSize);

        LambdaQueryWrapper<Job> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), Job::getJobName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Job::getUpdateTime);
        jobService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    @PostMapping("/checkJob")
    public R<String> checkJob(Long[] ids) {

        return null;
    }

}
