package com.example.novelbackend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Bookshelf {
    private Long id;
    private Integer userId;
    private Long novelId;
    private Integer lastReadChapterNum;      // 最后阅读章节号
    private LocalDateTime lastReadTime;  // 最后阅读时间
    private LocalDateTime createTime;    // 加入书架时间
}