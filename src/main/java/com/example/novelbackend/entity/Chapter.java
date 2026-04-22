package com.example.novelbackend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Chapter {
    private Long id;
    private Long novelId;
    private Integer chapterNum;
    private String title;
    private String content;
    private Integer wordCount;
    private Integer viewCount;
    private LocalDateTime createTime;
}