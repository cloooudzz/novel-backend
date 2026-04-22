package com.example.novelbackend.service;

import java.util.List;
import java.util.Map;

public interface BookshelfService {

    // 添加到书架
    Map<String, Object> addToBookshelf(Integer userId, Long novelId);

    // 移除书架
    Map<String, Object> removeFromBookshelf(Integer userId, Long novelId);

    // 获取用户书架列表
    List<Map<String, Object>> getUserBookshelf(Integer userId);

    // 更新阅读进度
    void updateReadProgress(Integer userId, Long novelId, Integer chapterNum);

    // 检查是否在书架中
    boolean isInBookshelf(Integer userId, Long novelId);
}