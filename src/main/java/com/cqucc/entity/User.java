package com.cqucc.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {
    public static final String DEFAULT_PASSWORD = "123456";

    private static final long serialVersionUID = 1L;

    //用户Id
    private Long Id;

    //用户账号
    private String username;

    //用户名
    private String name;

    private String password;

    //身份码 0:管理员 1:学生  2:公司
    private Integer Identity;

    //电话号码
    private String phone;

    //邮箱地址
    private String email;

    //头像
    private String image;

    //审核状态 0：待审核  1：审核通过  2：审核未通过
    private int status;

    //企业执照
    private String license;
}
