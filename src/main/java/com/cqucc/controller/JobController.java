package com.cqucc.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cqucc.common.BaseContext;
import com.cqucc.common.R;
import com.cqucc.dto.JobDto;
import com.cqucc.entity.CheckedJob;
import com.cqucc.entity.Job;
import com.cqucc.entity.User;
import com.cqucc.service.CategoryService;
import com.cqucc.service.CheckedJobService;
import com.cqucc.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/job")
public class JobController {
    @Autowired
    private JobService jobService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CheckedJobService checkedJobService;

    /**
     * 兼职信息分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<JobDto>> page(Integer page, Integer pageSize, String name) {
        Page<Job> pageInfo = new Page(page, pageSize);
        Page<JobDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Job> queryWrapper = new LambdaQueryWrapper<>();
        //审核状态为1--通过审核的
        queryWrapper.eq(Job::getStatus, 1);
        queryWrapper.like(StringUtils.isNotEmpty(name), Job::getJobName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Job::getUpdateTime);
        jobService.page(pageInfo, queryWrapper);

        //通过bean工具类复制属性
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        List<Job> records = pageInfo.getRecords();
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


    @PutMapping
    public R<String> updateJob(@RequestBody Job job, HttpSession session) {
        Object user = session.getAttribute("user");
        if (user.equals(job.getCompanyName())) {
            job.setStatus(0);
            jobService.updateById(job);
            return R.success("兼职信息修改成功,请等待管理员审核！");
        } else {
            return R.error("您不可以修改不属于您的信息！");
        }
    }

    /**
     * 新增兼职
     *
     * @param job
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Job job) {
        log.info(job.toString());
        job.setStatus(0);
        jobService.save(job);
        return R.success("添加成功！");
    }

    @DeleteMapping
    @Transactional
    public R<String> jobDelete(Long id) {
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<Job> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Job::getId, id);
        queryWrapper.eq(Job::getCreateUser, currentId);
        Job one = jobService.getOne(queryWrapper);
        if (one == null) {
            return R.error("您不能操作其他公司的兼职信息数据！");
        }

        jobService.remove(queryWrapper);
        return R.success("删除成功！");
    }

    /**
     * 根据id查找兼职
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public R<Job> queryById(@PathVariable Long id) {
        LambdaQueryWrapper<Job> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Job::getId, id);
        Job job = jobService.getById(id);
        return R.success(job);
    }
}
