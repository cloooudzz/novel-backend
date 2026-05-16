package com.example.novelbackend.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserTagPreference {
    private Long id;
    private Integer userId;
    private String tagName;
    private BigDecimal score;
    private LocalDateTime updatedAt;
}