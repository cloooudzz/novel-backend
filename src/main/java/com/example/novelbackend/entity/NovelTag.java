package com.example.novelbackend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NovelTag {
    private Long id;
    private Long novelId;
    private String tagName;
    private String tagCategory;
    private LocalDateTime createdAt;
}