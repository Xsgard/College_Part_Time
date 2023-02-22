package com.cqucc.dto;

import com.cqucc.entity.Job;
import lombok.Data;

@Data
public class JobDto extends Job {
    //分类名
    private String categoryName;
}
