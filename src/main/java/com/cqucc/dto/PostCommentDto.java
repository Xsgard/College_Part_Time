package com.cqucc.dto;

import com.cqucc.entity.Comment;
import com.cqucc.entity.Post;
import lombok.Data;

import java.util.List;

@Data
public class PostCommentDto extends Post {
    private List<Comment> comment;
}
