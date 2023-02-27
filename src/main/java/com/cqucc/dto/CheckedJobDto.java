package com.cqucc.dto;

import com.cqucc.entity.Job;
import lombok.Data;

@Data
public class CheckedJobDto extends Job {
    private Long userId;

    private String phone;

    private String email;

}
