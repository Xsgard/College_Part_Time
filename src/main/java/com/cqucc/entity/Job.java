package com.cqucc.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 兼职信息实体
 */
@Data
public class Job implements Serializable {
    private static final long serialVersionUID = 1L;

    //工作id
    private Long id;

    //工作名
    private String jobName;

    //工作类型
    private Long categoryId;

    //时薪
    private Double money;

    //公司名
    private String companyName;

    //工作地点
    private String location;

    //审核状态 0--未通过审核  1--通过审核  2--待审核
    private Integer status;

    //描述
    private String description;
    //创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


    //更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


    //创建人
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;


    //修改人
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;


}
