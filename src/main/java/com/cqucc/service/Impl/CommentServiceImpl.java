package com.cqucc.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqucc.entity.Comment;
import com.cqucc.mapper.CommentMapper;
import com.cqucc.service.CommentService;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService{
}
