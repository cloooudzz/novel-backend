package com.example.novelbackend.service.impl;

import com.example.novelbackend.entity.Category;
import com.example.novelbackend.entity.Chapter;
import com.example.novelbackend.entity.Novel;
import com.example.novelbackend.mapper.CategoryMapper;
import com.example.novelbackend.mapper.ChapterMapper;
import com.example.novelbackend.mapper.NovelMapper;
import com.example.novelbackend.service.NovelService;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NovelServiceImpl implements NovelService {

    @Resource
    private NovelMapper novelMapper;

    @Resource
    private ChapterMapper chapterMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public List<Novel> getAllNovels() {
        return novelMapper.findAll();
    }

    @Override
    public Map<String, Object> getNovelDetail(Long novelId) {
        // 1. 获取小说基本信息
        Novel novel = novelMapper.findById(novelId);
        if (novel == null) {
            return null;
        }

        // 2. 增加点击量
        novelMapper.incrementViewCount(novelId);

        // 3. 获取分类名称
        Category category = categoryMapper.findById(novel.getCategoryId());

        // 4. 获取总章节数
        int totalChapters = chapterMapper.countByNovelId(novelId);

        // 5. 获取最新章节
        Chapter lastChapter = null;
        List<Chapter> chapters = chapterMapper.findByNovelId(novelId);
        if (chapters != null && !chapters.isEmpty()) {
            lastChapter = chapters.get(chapters.size() - 1);
        }

        // 6. 组装返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("id", novel.getId());
        result.put("title", novel.getTitle());
        result.put("author", novel.getAuthor());
        result.put("categoryId", novel.getCategoryId());
        result.put("categoryName", category != null ? category.getName() : "未分类");
        result.put("cover", novel.getCover());
        result.put("intro", novel.getIntro());
        result.put("status", novel.getStatus());
        result.put("statusText", novel.getStatus() == 1 ? "连载中" : "已完结");
        result.put("viewCount", novel.getViewCount());
        result.put("collectCount", novel.getCollectCount());
        result.put("totalChapters", totalChapters);
        result.put("lastChapterId", lastChapter != null ? lastChapter.getId() : null);
        result.put("lastChapterTitle", lastChapter != null ? lastChapter.getTitle() : null);
        result.put("lastChapterNum", lastChapter != null ? lastChapter.getChapterNum() : null);

        return result;
    }

    @Override
    public List<Novel> getHotNovels(int limit) {
        return novelMapper.findHotNovels(limit);
    }

    @Override
    public List<Novel> getRecommendNovels(int limit) {
        return novelMapper.findRecommendNovels(limit);
    }

    @Override
    public List<Novel> getRankList(int limit) {
        return novelMapper.findRankByView(limit);
    }

    @Override
    public List<Chapter> getChapterList(Long novelId) {
        return chapterMapper.findByNovelId(novelId);
    }

    @Override
    public Chapter getChapterContent(Long novelId, Integer chapterNum) {
        Chapter chapter = chapterMapper.findByNovelIdAndChapterNum(novelId, chapterNum);
        if (chapter != null) {
            // 增加章节点击量
            chapterMapper.incrementViewCount(chapter.getId());
        }
        return chapter;
    }

    @Override
    public List<Novel> searchNovels(String keyword) {
        return novelMapper.search(keyword);
    }

    @Override
    public List<Novel> getNovelsByCategory(Integer categoryId) {
        return novelMapper.findByCategory(categoryId);
    }
}