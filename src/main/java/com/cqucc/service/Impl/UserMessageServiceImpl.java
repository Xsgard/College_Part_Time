package com.cqucc.service.Impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqucc.entity.UserMessage;
import com.cqucc.mapper.UserMessageMapper;
import com.cqucc.service.UserMessageService;
import org.springframework.stereotype.Service;


@Service
public class UserMessageServiceImpl extends ServiceImpl<UserMessageMapper, UserMessage> implements UserMessageService {
}
