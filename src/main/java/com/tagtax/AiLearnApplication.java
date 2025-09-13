package com.tagtax;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.tagtax.mapper")
public class AiLearnApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiLearnApplication.class, args);
    }

}
