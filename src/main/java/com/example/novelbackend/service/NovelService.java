package com.example.novelbackend.service;

import com.example.novelbackend.entity.Novel;
import com.example.novelbackend.entity.Chapter;
import java.util.List;
import java.util.Map;

public interface NovelService {

    // 获取所有小说
    List<Novel> getAllNovels();

    // 获取小说详情（包含分类名、总章节数、最新章节）
    Map<String, Object> getNovelDetail(Long novelId);

    // 获取热门推荐
    List<Novel> getHotNovels(int limit);

    // 获取推荐小说
    List<Novel> getRecommendNovels(int limit);

    // 获取排行榜
    List<Novel> getRankList(int limit);

    // 获取小说的章节列表
    List<Chapter> getChapterList(Long novelId);

    // 获取章节内容
    Chapter getChapterContent(Long novelId, Integer chapterNum);

    // 搜索小说
    List<Novel> searchNovels(String keyword);

    // 按分类获取小说
    List<Novel> getNovelsByCategory(Integer categoryId);
}