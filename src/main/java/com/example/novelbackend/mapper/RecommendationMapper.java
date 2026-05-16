package com.example.novelbackend.mapper;

import com.example.novelbackend.entity.Novel;
import com.example.novelbackend.entity.NovelTag;
import com.example.novelbackend.entity.UserTagPreference;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface RecommendationMapper {

    // 获取小说的所有标签
    List<NovelTag> getNovelTags(@Param("novelId") Long novelId);

    // 批量获取小说的标签
    List<NovelTag> getNovelsTags(@Param("novelIds") List<Long> novelIds);

    // 获取用户的所有标签偏好
    List<UserTagPreference> getUserTagPreferences(@Param("userId") Integer userId);

    // 更新或插入用户标签偏好
    int upsertUserTagPreference(@Param("userId") Integer userId,
                                @Param("tagName") String tagName,
                                @Param("score") BigDecimal score);

    // 记录用户行为
    int insertBehaviorLog(@Param("userId") Integer userId,
                          @Param("novelId") Long novelId,
                          @Param("behaviorType") String behaviorType,
                          @Param("weight") Integer weight);

    // 获取用户最近阅读的小说标签（用于推荐）
    List<String> getUserRecentNovelTags(@Param("userId") Integer userId, @Param("limit") int limit);

    // 基于标签匹配推荐小说
    List<Novel> recommendByTags(@Param("tags") List<String> tags,
                                @Param("userId") Integer userId,
                                @Param("limit") int limit);

    // 获取热门标签（冷启动用）
    List<Map<String, Object>> getHotTags(@Param("limit") int limit);

    // 获取所有可用标签列表
    List<String> getAllTags();

    // 按类型分组获取标签
    List<Map<String, Object>> getTagsByCategory();
}