package com.cqucc.dto;

import com.cqucc.entity.Comment;
import com.cqucc.entity.Post;
import lombok.Data;

import java.io.Serializable;

@Data
public class ForumDto extends Post {

    private Comment comment;
}
