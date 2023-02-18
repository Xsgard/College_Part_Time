package com.cqucc.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqucc.entity.Job;
import com.cqucc.mapper.JobMapper;
import com.cqucc.service.JobService;
import org.springframework.stereotype.Service;

@Service
public class JobServiceImpl extends ServiceImpl<JobMapper, Job> implements JobService {
}
