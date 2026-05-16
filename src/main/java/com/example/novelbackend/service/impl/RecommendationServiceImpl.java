package com.example.novelbackend.service.impl;

import com.example.novelbackend.entity.Novel;
import com.example.novelbackend.entity.NovelTag;
import com.example.novelbackend.entity.UserTagPreference;
import com.example.novelbackend.mapper.*;
import com.example.novelbackend.service.RecommendationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.annotation.Lazy;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    @Resource
    private RecommendationMapper recommendationMapper;

    @Resource
    private BookshelfMapper bookshelfMapper;

    // 行为权重配置
    private static final Map<String, Integer> BEHAVIOR_WEIGHTS = new HashMap<>();
    static {
        BEHAVIOR_WEIGHTS.put("add_to_shelf", 8);
        BEHAVIOR_WEIGHTS.put("read_chapter", 2);
        BEHAVIOR_WEIGHTS.put("comment", 5);
        BEHAVIOR_WEIGHTS.put("like_comment", 1);
    }

    @Override
    public List<Novel> getPersonalizedRecommendations(Integer userId, int limit) {
        List<String> userPreferTags = getUserPreferTags(userId);

        if (userPreferTags.isEmpty()) {
            return getColdStartRecommendations(limit);
        }

        return recommendationMapper.recommendByTags(userPreferTags, userId, limit);
    }

    private List<String> getUserPreferTags(Integer userId) {
        if (userId == null) return Collections.emptyList();

        var preferences = recommendationMapper.getUserTagPreferences(userId);
        if (preferences == null || preferences.isEmpty()) {
            return Collections.emptyList();
        }

        return preferences.stream()
                .sorted(Comparator.comparing(p -> ((UserTagPreference) p).getScore()).reversed())
                .limit(5)
                .map(UserTagPreference::getTagName)
                .collect(Collectors.toList());
    }

    private List<Novel> getColdStartRecommendations(int limit) {
        return recommendationMapper.recommendByTags(Collections.emptyList(), null, limit);
    }

    @Override
    @Transactional
    public void recordUserBehavior(Integer userId, Long novelId, String behaviorType) {
        System.out.println("===== recordUserBehavior 被调用 =====");
        System.out.println("userId: " + userId + ", novelId: " + novelId + ", type: " + behaviorType);

        if (userId == null || novelId == null) {
            System.out.println("参数为空，跳过记录");
            return;
        }

        Integer weight = BEHAVIOR_WEIGHTS.getOrDefault(behaviorType, 1);
        System.out.println("行为权重: " + weight);

        try {
            int result = recommendationMapper.insertBehaviorLog(userId, novelId, behaviorType, weight);
            System.out.println("插入行为日志结果: " + result);
        } catch (Exception e) {
            System.err.println("插入失败: " + e.getMessage());
            e.printStackTrace();
        }

        updateUserTagPreferences(userId);
    }

    @Override
    public List<Map<String, Object>> getUserTagPreferences(Integer userId) {
        if (userId == null) return Collections.emptyList();

        var preferences = recommendationMapper.getUserTagPreferences(userId);
        List<Map<String, Object>> result = new ArrayList<>();

        if (preferences != null) {
            for (var pref : preferences) {
                Map<String, Object> item = new HashMap<>();
                item.put("tagName", pref.getTagName());
                item.put("score", pref.getScore());
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public List<String> getAllTags() {
        return recommendationMapper.getAllTags();
    }

    @Override
    public Map<String, List<String>> getTagsByCategory() {
        List<Map<String, Object>> tags = recommendationMapper.getTagsByCategory();
        Map<String, List<String>> result = new LinkedHashMap<>();

        for (Map<String, Object> tag : tags) {
            String category = (String) tag.get("tag_category");
            String tagName = (String) tag.get("tag_name");
            if (category == null) category = "other";

            result.computeIfAbsent(category, k -> new ArrayList<>()).add(tagName);
        }
        return result;
    }

    @Override
    @Transactional
    public void updateUserTagPreferences(Integer userId) {
        List<Map<String, Object>> userBooks = getUserBehaviorBooks(userId);

        Map<String, BigDecimal> tagScores = new HashMap<>();

        for (Map<String, Object> book : userBooks) {
            // 👇 修复：安全地获取 novelId，支持 Integer 和 Long
            Object novelIdObj = book.get("novelId");
            Long novelId = null;
            if (novelIdObj instanceof Integer) {
                novelId = ((Integer) novelIdObj).longValue();
            } else if (novelIdObj instanceof Long) {
                novelId = (Long) novelIdObj;
            } else if (novelIdObj instanceof Number) {
                novelId = ((Number) novelIdObj).longValue();
            }

            if (novelId == null) continue;

            Object weightObj = book.get("totalWeight");
            Integer totalWeight = null;
            if (weightObj instanceof Integer) {
                totalWeight = (Integer) weightObj;
            } else if (weightObj instanceof Number) {
                totalWeight = ((Number) weightObj).intValue();
            }

            if (totalWeight == null) continue;

            List<NovelTag> tags = recommendationMapper.getNovelTags(novelId);

            for (NovelTag tag : tags) {
                BigDecimal score = BigDecimal.valueOf(totalWeight);
                tagScores.merge(tag.getTagName(), score, BigDecimal::add);
            }
        }

        for (Map.Entry<String, BigDecimal> entry : tagScores.entrySet()) {
            BigDecimal normalizedScore = entry.getValue().min(BigDecimal.valueOf(100));
            try {
                recommendationMapper.upsertUserTagPreference(userId, entry.getKey(), normalizedScore);
            } catch (Exception e) {
                System.err.println("更新标签偏好失败: " + e.getMessage());
            }
        }
    }

    private List<Map<String, Object>> getUserBehaviorBooks(Integer userId) {
        List<Map<String, Object>> result = new ArrayList<>();

        try {
            List<Map<String, Object>> shelfBooks = bookshelfMapper.findUserBookshelfWithNovelInfo(userId);
            for (var book : shelfBooks) {
                Map<String, Object> item = new HashMap<>();
                // 👇 修复：安全获取 novelId
                Object novelIdObj = book.get("novelId");
                if (novelIdObj != null) {
                    item.put("novelId", novelIdObj);
                    item.put("totalWeight", 8);
                    result.add(item);
                }
            }
        } catch (Exception e) {
            System.err.println("获取用户书架失败: " + e.getMessage());
        }

        return result;
    }
}