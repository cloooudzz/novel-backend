package com.example.novelbackend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;
    private Integer userId;
    private Long novelId;
    private Integer chapterNum;
    private String content;
    private Integer likeCount;
    private Integer replyCount;
    private Long parentId;
    private Integer status;
    private LocalDateTime createTime;

    // 关联字段
    private String username;
    private String userAvatar;
    private Boolean isLiked;  // 当前用户是否点赞
    private Integer novelChapterNum;  // 章节号（用于展示）
}