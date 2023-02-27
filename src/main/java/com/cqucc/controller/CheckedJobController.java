package com.cqucc.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cqucc.common.BaseContext;
import com.cqucc.common.R;
import com.cqucc.dto.CheckedJobDto;
import com.cqucc.entity.CheckedJob;
import com.cqucc.entity.Job;
import com.cqucc.entity.User;
import com.cqucc.service.CheckedJobService;
import com.cqucc.service.JobService;
import com.cqucc.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/checkedJob")
@Slf4j
public class CheckedJobController {

    @Autowired
    private JobService jobService;

    @Autowired
    private UserService userService;

    @Autowired
    private CheckedJobService checkedJobService;

    /**
     * 已选兼职信息分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<CheckedJobDto>> page(Integer page, Integer pageSize, String name) {
        Page<CheckedJob> pageInfo = new Page<>(page, pageSize);
        Page<CheckedJobDto> dtoPage = new Page<>();
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<CheckedJob> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CheckedJob::getCreateUser, userId);
        queryWrapper.like(StringUtils.isNotEmpty(name), CheckedJob::getJobName, name);
        queryWrapper.orderByDesc(CheckedJob::getUpdateTime);
        checkedJobService.page(pageInfo, queryWrapper);

        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        List<CheckedJob> records = pageInfo.getRecords();
        List<CheckedJobDto> dtoList = records.stream().map((item) -> {
            CheckedJobDto c = new CheckedJobDto();
            BeanUtils.copyProperties(item, c);
            Long jobId = item.getJobId();
            Long createUserId = item.getCreateUser();
            Job job = jobService.getById(jobId);
            User user = userService.getById(createUserId);
            c.setUserId(job.getCreateUser());
            c.setMoney(job.getMoney());
            c.setEmail(user.getEmail());
            c.setPhone(user.getPhone());
            c.setDescription(job.getDescription());
            c.setLocation(job.getLocation());
            return c;
        }).collect(Collectors.toList());
        dtoPage.setRecords(dtoList);

        return R.success(dtoPage);
    }

    @DeleteMapping
    @Transactional
    public R<String> checkedJobDel(Long[] ids) {
        if (ids.length == 1) {
            checkedJobService.removeById(ids);
        } else {
            for (Long id :
                    ids) {
                checkedJobService.removeById(id);
            }
        }
        return R.success("取消成功！");
    }

    /**
     * 选择兼职
     *
     * @param ids
     * @return
     */
    @GetMapping("/{ids}")
    public R<String> checkJob(@PathVariable Long[] ids) {
        CheckedJob c = new CheckedJob();
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<CheckedJob> queryWrapper = new LambdaQueryWrapper<>();
        for (Long id : ids) {
            queryWrapper.eq(CheckedJob::getJobId, id);
            queryWrapper.eq(CheckedJob::getCreateUser, userId);
            int i = checkedJobService.count(queryWrapper);
            if (i > 0) {
                return R.error("您已应聘过该公司，可在‘ 我的兼职 ’页面中查看信息 ！");
            }
        }

        if (ids.length == 1) {
            Long id = ids[0];
            if (jobCheck(c, id)) return R.success("应聘成功！");
        }
        if (ids.length > 1) {
            for (Long id : ids) {

                if (jobCheck(c, id)) return R.success("应聘成功！");
            }
        }
        return R.error("应聘失败！");
    }

    private boolean jobCheck(CheckedJob c, Long id) {
        Job job = jobService.getById(id);
        if (job != null) {
            c.setJobId(job.getId());
            c.setCompanyName(job.getCompanyName());
            c.setJobName(job.getJobName());
            checkedJobService.save(c);
            return true;
        }
        return false;
    }

}
