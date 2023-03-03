package com.cqucc.entity;

import lombok.Data;

@Data
public class ResultMessage {
    //判断是否为系统消息--是为true
    private boolean isSystem;
    //若为系统消息则为null，反之为发送给的用户名
    private String fromName;
    //系统消息为数组结构
    private Object message;
}
