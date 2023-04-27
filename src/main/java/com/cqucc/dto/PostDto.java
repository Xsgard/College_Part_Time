package com.cqucc.dto;

import com.cqucc.entity.Post;
import lombok.Data;

@Data
public class PostDto extends Post {
    //分类名
    private String categoryName;
    //用户名
    private String poster;
}
