package com.example.novelbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.novelbackend.mapper")  //扫描mapper包下的所有接口
public class NovelBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(NovelBackendApplication.class, args);
	}
}