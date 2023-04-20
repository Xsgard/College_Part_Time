package com.cqucc.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMessage {
    private String username;
    private String message;//聊天文本
    private String toname;
    private String createtime;
}
