package com.cqucc.dto;

import com.cqucc.entity.Job;
import lombok.Data;

@Data
public class CheckedJobDto extends Job {

    //用户ID
    private Long userId;
    //用户名
    private String userName;
    //工作分类
    private String categoryName;
    //电话号码
    private String phone;
    //邮箱
    private String email;
    //地点
    private String location;
    //描述
    private String description;

}
