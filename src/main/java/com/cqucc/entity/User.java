package com.cqucc.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    //用户Id
    private Long Id;

    //用户名
    private String username;

    //身份码 1:学生  2:公司
    private Integer Identity;

    //电话号码
    private String phoneNum;

    //邮箱地址
    private String email;

    //图片
    private String image;

    //审核状态 0：待审核  1：审核通过  2：审核未通过
    private int status;

    //企业执照
    private String license;
}
