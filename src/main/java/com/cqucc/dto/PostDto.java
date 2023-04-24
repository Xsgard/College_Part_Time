package com.cqucc.dto;

import com.cqucc.entity.Post;
import lombok.Data;

@Data
public class PostDto extends Post {
    private String categoryName;
}
