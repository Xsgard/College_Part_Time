package com.cqucc.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 兼职信息实体
 */
@Data
public class Job {
    //工作id
    private Long id;

    //工作名
    private String jobName;

    //工作类型
    private String type;

    //时薪
    private Double money;

    //公司名
    private String companyName;

    //工作地点
    private String location;

    //最后更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    //描述
    private String description;

}
