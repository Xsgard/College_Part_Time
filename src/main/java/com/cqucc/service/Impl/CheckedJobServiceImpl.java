package com.cqucc.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqucc.entity.CheckedJob;
import com.cqucc.mapper.CheckedJobMapper;
import com.cqucc.service.CheckedJobService;
import org.springframework.stereotype.Service;

@Service
public class CheckedJobServiceImpl extends ServiceImpl<CheckedJobMapper, CheckedJob> implements CheckedJobService {
}
