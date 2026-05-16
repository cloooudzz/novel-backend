package com.example.novelbackend.controller;

import com.example.novelbackend.entity.Novel;
import com.example.novelbackend.service.RecommendationService;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/recommend")
public class RecommendationController {

    @Resource
    private RecommendationService recommendationService;

    // 获取个性化推荐
    @GetMapping("/personal")
    public Map<String, Object> getPersonalizedRecommendations(
            @RequestParam(required = false) Integer userId,
            @RequestParam(defaultValue = "10") int limit) {
        Map<String, Object> result = new HashMap<>();

        List<Novel> novels;
        if (userId != null) {
            novels = recommendationService.getPersonalizedRecommendations(userId, limit);
        } else {
            // 未登录用户返回热门推荐
            novels = recommendationService.getPersonalizedRecommendations(null, limit);
        }

        result.put("code", 200);
        result.put("data", novels);
        return result;
    }

    // 获取用户标签偏好
    @GetMapping("/preferences")
    public Map<String, Object> getUserPreferences(@RequestParam Integer userId) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", recommendationService.getUserTagPreferences(userId));
        return result;
    }

    // 获取所有可用标签
    @GetMapping("/tags")
    public Map<String, Object> getAllTags() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", recommendationService.getTagsByCategory());
        return result;
    }

    // 记录用户行为（前端调用）
    @PostMapping("/behavior")
    public Map<String, Object> recordBehavior(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();

        Integer userId = (Integer) params.get("userId");
        Long novelId = ((Number) params.get("novelId")).longValue();
        String behaviorType = (String) params.get("behaviorType");

        if (userId != null && novelId != null && behaviorType != null) {
            recommendationService.recordUserBehavior(userId, novelId, behaviorType);
        }

        result.put("code", 200);
        return result;
    }
}