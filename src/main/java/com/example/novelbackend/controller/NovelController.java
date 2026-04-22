package com.example.novelbackend.controller;

import com.example.novelbackend.entity.Chapter;
import com.example.novelbackend.entity.Novel;
import com.example.novelbackend.mapper.ChapterMapper;
import com.example.novelbackend.service.BookshelfService;
import com.example.novelbackend.service.NovelService;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/novel")
public class NovelController {

    @Resource
    private NovelService novelService;
    @Resource
    private ChapterMapper chapterMapper;
    @Resource
    private BookshelfService bookshelfService;

    // 获取所有小说（书城用）
    @GetMapping("/list")
    public Map<String, Object> getNovelList() {
        Map<String, Object> result = new HashMap<>();
        List<Novel> list = novelService.getAllNovels();
        result.put("code", 200);
        result.put("data", list);
        return result;
    }

    // 获取小说详情
    @GetMapping("/detail/{id}")
    public Map<String, Object> getNovelDetail(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> detail = novelService.getNovelDetail(id);
        if (detail != null) {
            result.put("code", 200);
            result.put("data", detail);
        } else {
            result.put("code", 404);
            result.put("msg", "小说不存在");
        }
        return result;
    }

    // 获取热门推荐
    @GetMapping("/hot")
    public Map<String, Object> getHotNovels(@RequestParam(defaultValue = "8") int limit) {
        Map<String, Object> result = new HashMap<>();
        List<Novel> list = novelService.getHotNovels(limit);
        result.put("code", 200);
        result.put("data", list);
        return result;
    }

    // 获取推荐小说
    @GetMapping("/recommend")
    public Map<String, Object> getRecommendNovels(@RequestParam(defaultValue = "8") int limit) {
        Map<String, Object> result = new HashMap<>();
        List<Novel> list = novelService.getRecommendNovels(limit);
        result.put("code", 200);
        result.put("data", list);
        return result;
    }

    // 获取排行榜
    @GetMapping("/rank")
    public Map<String, Object> getRankList(@RequestParam(defaultValue = "10") int limit) {
        Map<String, Object> result = new HashMap<>();
        List<Novel> list = novelService.getRankList(limit);
        result.put("code", 200);
        result.put("data", list);
        return result;
    }

    // 获取小说章节列表
    @GetMapping("/chapters/{novelId}")
    public Map<String, Object> getChapters(@PathVariable Long novelId) {
        Map<String, Object> result = new HashMap<>();
        List<Chapter> list = novelService.getChapterList(novelId);
        result.put("code", 200);
        result.put("data", list);
        result.put("total", list.size());
        return result;
    }

    // 获取章节内容
    @GetMapping("/chapter")
    public Map<String, Object> getChapterContent(
            @RequestParam Long novelId,
            @RequestParam Integer chapterNum) {
        Map<String, Object> result = new HashMap<>();
        Chapter chapter = novelService.getChapterContent(novelId, chapterNum);
        if (chapter != null) {
            result.put("code", 200);
            result.put("data", chapter);
        } else {
            result.put("code", 404);
            result.put("msg", "章节不存在");
        }
        return result;
    }

    // 搜索小说
    @GetMapping("/search")
    public Map<String, Object> search(@RequestParam String keyword) {
        Map<String, Object> result = new HashMap<>();
        List<Novel> list = novelService.searchNovels(keyword);
        result.put("code", 200);
        result.put("data", list);
        result.put("keyword", keyword);
        return result;
    }

    // 按分类获取小说
    @GetMapping("/category/{categoryId}")
    public Map<String, Object> getByCategory(@PathVariable Integer categoryId) {
        Map<String, Object> result = new HashMap<>();
        List<Novel> list = novelService.getNovelsByCategory(categoryId);
        result.put("code", 200);
        result.put("data", list);
        return result;
    }

    // 添加到书架
    @PostMapping("/bookshelf/add")
    public Map<String, Object> addToBookshelf(@RequestBody Map<String, Integer> params) {
        Map<String, Object> result = new HashMap<>();

        Integer userId = params.get("userId");
        Integer novelIdInt = params.get("novelId");

        if (userId == null || novelIdInt == null) {
            result.put("code", 400);
            result.put("msg", "参数错误");
            return result;
        }

        Long novelId = novelIdInt.longValue();
        Map<String, Object> serviceResult = bookshelfService.addToBookshelf(userId, novelId);

        if ((Boolean) serviceResult.get("success")) {
            result.put("code", 200);
            result.put("msg", serviceResult.get("msg"));
        } else {
            result.put("code", 400);
            result.put("msg", serviceResult.get("msg"));
        }

        return result;
    }

    // 获取章节内容（包含导航信息）- 带阅读进度更新
    @GetMapping("/chapter/detail")
    public Map<String, Object> getChapterDetail(
            @RequestParam Long novelId,
            @RequestParam Integer chapterNum,
            @RequestParam(required = false) Integer userId) {  // 添加 userId 参数
        Map<String, Object> result = new HashMap<>();

        // 获取当前章节
        Chapter currentChapter = novelService.getChapterContent(novelId, chapterNum);
        if (currentChapter == null) {
            result.put("code", 404);
            result.put("msg", "章节不存在");
            return result;
        }

        // 获取小说信息
        Map<String, Object> novelDetail = novelService.getNovelDetail(novelId);

        // 获取总章节数
        int totalChapters = chapterMapper.countByNovelId(novelId);

        // 获取上一章
        Chapter prevChapter = chapterMapper.getPrevChapter(novelId, chapterNum);

        // 获取下一章
        Chapter nextChapter = chapterMapper.getNextChapter(novelId, chapterNum);

        // 组装返回数据
        Map<String, Object> data = new HashMap<>();
        data.put("novelId", novelId);
        data.put("novelTitle", novelDetail.get("title"));
        data.put("chapterId", currentChapter.getId());
        data.put("chapterNum", currentChapter.getChapterNum());
        data.put("title", currentChapter.getTitle());
        data.put("content", currentChapter.getContent());
        data.put("wordCount", currentChapter.getWordCount());
        data.put("viewCount", currentChapter.getViewCount());
        data.put("createTime", currentChapter.getCreateTime());
        data.put("totalChapters", totalChapters);

        // 上一章信息
        if (prevChapter != null) {
            data.put("prevChapterNum", prevChapter.getChapterNum());
            data.put("prevChapterTitle", prevChapter.getTitle());
        } else {
            data.put("prevChapterNum", null);
            data.put("prevChapterTitle", null);
        }

        // 下一章信息
        if (nextChapter != null) {
            data.put("nextChapterNum", nextChapter.getChapterNum());
            data.put("nextChapterTitle", nextChapter.getTitle());
        } else {
            data.put("nextChapterNum", null);
            data.put("nextChapterTitle", null);
        }

        // 如果用户已登录，更新阅读进度
        if (userId != null) {
            bookshelfService.updateReadProgress(userId, novelId, chapterNum);
        }

        result.put("code", 200);
        result.put("data", data);
        return result;
    }
}