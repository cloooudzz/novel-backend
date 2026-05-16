package com.example.novelbackend.service;

import com.example.novelbackend.entity.Novel;
import java.util.List;
import java.util.Map;

public interface RecommendationService {

    // 为用户生成个性化推荐
    List<Novel> getPersonalizedRecommendations(Integer userId, int limit);

    // 记录用户行为（用于计算偏好）
    void recordUserBehavior(Integer userId, Long novelId, String behaviorType);

    // 获取用户标签偏好
    List<Map<String, Object>> getUserTagPreferences(Integer userId);

    // 获取所有可用标签
    List<String> getAllTags();

    // 获取按类型分组的标签
    Map<String, List<String>> getTagsByCategory();

    // 根据用户行为计算并更新标签偏好
    void updateUserTagPreferences(Integer userId);
}