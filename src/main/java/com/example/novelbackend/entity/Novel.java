package com.example.novelbackend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Novel {
    private Long id;
    private String title;
    private String author;
    private Integer categoryId;
    private String cover;
    private String intro;
    private Integer status;  // 0-完结,1-连载中
    private Integer viewCount;
    private Integer collectCount;
    private Integer isRecommend;
    private Integer isHot;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 关联字段（非数据库字段）
    private String categoryName;  // 分类名称
    private Integer totalChapters;  // 总章节数
    private String lastChapterTitle;  // 最新章节标题
}