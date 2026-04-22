package com.example.novelbackend.mapper;

import com.example.novelbackend.entity.Novel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface NovelMapper {

    // 获取所有小说（书城用）
    List<Novel> findAll();

    // 根据ID获取小说详情
    Novel findById(@Param("id") Long id);

    // 获取热门推荐小说
    List<Novel> findHotNovels(@Param("limit") int limit);

    // 获取推荐小说
    List<Novel> findRecommendNovels(@Param("limit") int limit);

    // 获取排行榜（按点击量）
    List<Novel> findRankByView(@Param("limit") int limit);

    // 按分类获取小说
    List<Novel> findByCategory(@Param("categoryId") Integer categoryId);

    // 增加点击量
    int incrementViewCount(@Param("id") Long id);

    // 搜索小说
    List<Novel> search(@Param("keyword") String keyword);

    // 获取小说总章节数
    int getChapterCount(@Param("novelId") Long novelId);

    // 获取最新章节标题
    String getLastChapterTitle(@Param("novelId") Long novelId);
}