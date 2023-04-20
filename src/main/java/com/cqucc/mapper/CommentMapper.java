package com.cqucc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqucc.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
