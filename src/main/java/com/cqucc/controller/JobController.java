package com.cqucc.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cqucc.common.R;
import com.cqucc.dto.JobDto;
import com.cqucc.entity.CheckedJob;
import com.cqucc.entity.Job;
import com.cqucc.service.CategoryService;
import com.cqucc.service.CheckedJobService;
import com.cqucc.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    /**
     * 选择兼职
     *
     * @param ids
     * @return
     */
    @PostMapping("/checkJob")
    public R<String> checkJob(Long[] ids) {
        CheckedJob c = new CheckedJob();
        for (Long id : ids) {
            Job job = jobService.getById(id);
            if (job != null) {
                c.setJobId(job.getId());
                c.setCompanyName(job.getCompanyName());
                c.setJobName(job.getJobName());

                checkedJobService.save(c);
                return R.success("应聘成功！");
            }

        }
        return R.error("应聘失败！");
    }

}
