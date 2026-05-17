package com.example.novelbackend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Author {
    private Long id;
    private Integer userId;
    private String penName;
    private String contact;
    private String genre;
    private String intro;
    private Integer status;  // 1-正常,0-禁用
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}