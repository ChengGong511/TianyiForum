package com.tianyi.tianyiforumbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.tianyi.tianyiforumbackend.Mapper")
public class TianyiForumBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(TianyiForumBackendApplication.class, args);
    }

}
