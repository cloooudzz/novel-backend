package com.example.novelbackend.controller;

import com.example.novelbackend.entity.Novel;
import com.example.novelbackend.service.RecommendationService;
import jakarta.servlet.http.HttpServletRequest;
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

    private Integer getUserIdFromRequest(HttpServletRequest request) {
        return (Integer) request.getAttribute("userId");
    }

    // 获取个性化推荐
    @GetMapping("/personal")
    public Map<String, Object> getPersonalizedRecommendations(
            HttpServletRequest request,
            @RequestParam(defaultValue = "10") int limit) {
        Map<String, Object> result = new HashMap<>();

        Integer userId = getUserIdFromRequest(request);
        List<Novel> novels;
        if (userId != null) {
            novels = recommendationService.getPersonalizedRecommendations(userId, limit);
        } else {
            novels = recommendationService.getPersonalizedRecommendations(null, limit);
        }

        result.put("code", 200);
        result.put("data", novels);
        return result;
    }

    // 获取用户标签偏好
    @GetMapping("/preferences")
    public Map<String, Object> getUserPreferences(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Integer userId = getUserIdFromRequest(request);

        if (userId == null) {
            result.put("code", 401);
            result.put("msg", "请先登录");
            return result;
        }

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
    public Map<String, Object> recordBehavior(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();

        Integer userId = getUserIdFromRequest(request);
        if (userId == null) {
            result.put("code", 401);
            result.put("msg", "请先登录");
            return result;
        }

        Long novelId = ((Number) params.get("novelId")).longValue();
        String behaviorType = (String) params.get("behaviorType");

        if (novelId != null && behaviorType != null) {
            recommendationService.recordUserBehavior(userId, novelId, behaviorType);
        }

        result.put("code", 200);
        return result;
    }
}