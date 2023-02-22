package com.cqucc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class CollegePartTimeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollegePartTimeApplication.class, args);
        log.info("项目启动成功...");
    }

}
