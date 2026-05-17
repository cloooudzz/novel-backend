package com.example.novelbackend.controller;

import com.example.novelbackend.entity.Author;
import com.example.novelbackend.service.AuthorService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/author")
public class AuthorController {

    @Resource
    private AuthorService authorService;

    // 申请成为作者
    @PostMapping("/apply")
    public Map<String, Object> applyForAuthor(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();

        Integer userId = (Integer) params.get("userId");
        String penName = (String) params.get("penName");
        String contact = (String) params.get("contact");
        String genre = (String) params.get("genre");
        String intro = (String) params.get("intro");

        if (userId == null || penName == null || penName.trim().isEmpty()) {
            result.put("code", 400);
            result.put("msg", "参数错误");
            return result;
        }

        Map<String, Object> serviceResult = authorService.applyForAuthor(userId, penName, contact, genre, intro);

        if ((Boolean) serviceResult.get("success")) {
            result.put("code", 200);
            result.put("msg", serviceResult.get("msg"));
            result.put("penName", serviceResult.get("penName"));
        } else {
            result.put("code", 400);
            result.put("msg", serviceResult.get("msg"));
        }
        return result;
    }

    // 检查是否是作者
    @GetMapping("/check")
    public Map<String, Object> checkAuthor(@RequestParam Integer userId) {
        Map<String, Object> result = new HashMap<>();
        boolean isAuthor = authorService.isAuthor(userId);
        result.put("code", 200);
        result.put("isAuthor", isAuthor);
        if (isAuthor) {
            Author author = authorService.getAuthorByUserId(userId);
            result.put("penName", author != null ? author.getPenName() : null);
        }
        return result;
    }

    // 获取作者信息
    @GetMapping("/info")
    public Map<String, Object> getAuthorInfo(@RequestParam Integer userId) {
        Map<String, Object> result = new HashMap<>();
        Author author = authorService.getAuthorByUserId(userId);
        if (author == null) {
            result.put("code", 404);
            result.put("msg", "您还不是作者");
            return result;
        }
        result.put("code", 200);
        result.put("data", author);
        return result;
    }

    // 获取作者的小说列表
    @GetMapping("/novels")
    public Map<String, Object> getAuthorNovels(@RequestParam Integer userId) {
        Map<String, Object> result = new HashMap<>();
        if (!authorService.isAuthor(userId)) {
            result.put("code", 403);
            result.put("msg", "您还不是作者");
            return result;
        }
        List<Map<String, Object>> novels = authorService.getAuthorNovels(userId);
        result.put("code", 200);
        result.put("data", novels);
        return result;
    }

    // 创建新小说
    @PostMapping("/novel/create")
    public Map<String, Object> createNovel(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();

        System.out.println("=== createNovel 请求参数 ===");
        Integer userId = (Integer) params.get("userId");
        String title = (String) params.get("title");
        String authorName = (String) params.get("authorName");
        Integer categoryId = params.get("categoryId") != null ? ((Number) params.get("categoryId")).intValue() : null;
        String cover = (String) params.get("cover");
        String intro = (String) params.get("intro");
        Integer status = params.get("status") != null ? ((Number) params.get("status")).intValue() : 1;

        if (userId == null || title == null || title.trim().isEmpty()) {
            result.put("code", 400);
            result.put("msg", "参数错误");
            return result;
        }

        Map<String, Object> serviceResult = authorService.createNovel(userId, title, authorName, categoryId, cover, intro, status);

        if ((Boolean) serviceResult.get("success")) {
            result.put("code", 200);
            result.put("msg", serviceResult.get("msg"));
            result.put("novelId", serviceResult.get("novelId"));
        } else {
            result.put("code", 400);
            result.put("msg", serviceResult.get("msg"));
        }
        return result;
    }

    // 更新小说
    @PutMapping("/novel/update")
    public Map<String, Object> updateNovel(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();

        Long novelId = ((Number) params.get("novelId")).longValue();
        Integer userId = (Integer) params.get("userId");
        String title = (String) params.get("title");
        Integer categoryId = params.get("categoryId") != null ? ((Number) params.get("categoryId")).intValue() : null;
        String cover = (String) params.get("cover");
        String intro = (String) params.get("intro");
        Integer status = params.get("status") != null ? ((Number) params.get("status")).intValue() : null;

        Map<String, Object> serviceResult = authorService.updateNovel(novelId, userId, title, categoryId, cover, intro, status);

        if ((Boolean) serviceResult.get("success")) {
            result.put("code", 200);
            result.put("msg", serviceResult.get("msg"));
        } else {
            result.put("code", 400);
            result.put("msg", serviceResult.get("msg"));
        }
        return result;
    }

    // 删除小说
    @DeleteMapping("/novel/delete")
    public Map<String, Object> deleteNovel(@RequestParam Long novelId, @RequestParam Integer userId) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> serviceResult = authorService.deleteNovel(novelId, userId);
        if ((Boolean) serviceResult.get("success")) {
            result.put("code", 200);
            result.put("msg", serviceResult.get("msg"));
        } else {
            result.put("code", 400);
            result.put("msg", serviceResult.get("msg"));
        }
        return result;
    }

    // 添加章节
    @PostMapping("/chapter/add")
    public Map<String, Object> addChapter(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();

        Long novelId = ((Number) params.get("novelId")).longValue();
        Integer userId = (Integer) params.get("userId");
        Integer chapterNum = ((Number) params.get("chapterNum")).intValue();
        String title = (String) params.get("title");
        String content = (String) params.get("content");

        if (novelId == null || userId == null || chapterNum == null || title == null) {
            result.put("code", 400);
            result.put("msg", "参数错误");
            return result;
        }

        Map<String, Object> serviceResult = authorService.addChapter(novelId, userId, chapterNum, title, content != null ? content : "");

        if ((Boolean) serviceResult.get("success")) {
            result.put("code", 200);
            result.put("msg", serviceResult.get("msg"));
            result.put("chapterId", serviceResult.get("chapterId"));
        } else {
            result.put("code", 400);
            result.put("msg", serviceResult.get("msg"));
        }
        return result;
    }

    // 更新章节
    @PutMapping("/chapter/update")
    public Map<String, Object> updateChapter(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();

        Long chapterId = ((Number) params.get("chapterId")).longValue();
        Integer userId = (Integer) params.get("userId");
        String title = (String) params.get("title");
        String content = (String) params.get("content");

        Map<String, Object> serviceResult = authorService.updateChapter(chapterId, userId, title, content);

        if ((Boolean) serviceResult.get("success")) {
            result.put("code", 200);
            result.put("msg", serviceResult.get("msg"));
        } else {
            result.put("code", 400);
            result.put("msg", serviceResult.get("msg"));
        }
        return result;
    }

    // 删除章节
    @DeleteMapping("/chapter/delete")
    public Map<String, Object> deleteChapter(@RequestParam Long chapterId, @RequestParam Integer userId) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> serviceResult = authorService.deleteChapter(chapterId, userId);
        if ((Boolean) serviceResult.get("success")) {
            result.put("code", 200);
            result.put("msg", serviceResult.get("msg"));
        } else {
            result.put("code", 400);
            result.put("msg", serviceResult.get("msg"));
        }
        return result;
    }

    // 获取小说的章节列表
    @GetMapping("/chapters")
    public Map<String, Object> getChapters(@RequestParam Long novelId, @RequestParam Integer userId) {
        Map<String, Object> result = new HashMap<>();
        List<com.example.novelbackend.entity.Chapter> chapters = authorService.getChaptersByNovel(novelId, userId);
        if (chapters == null) {
            result.put("code", 403);
            result.put("msg", "无权查看该小说章节");
            return result;
        }
        result.put("code", 200);
        result.put("data", chapters);
        return result;
    }

    // 获取章节详情
    @GetMapping("/chapter/detail")
    public Map<String, Object> getChapterDetail(@RequestParam Long chapterId, @RequestParam Integer userId) {
        Map<String, Object> result = new HashMap<>();
        com.example.novelbackend.entity.Chapter chapter = authorService.getChapterDetail(chapterId, userId);
        if (chapter == null) {
            result.put("code", 404);
            result.put("msg", "章节不存在或无权限");
            return result;
        }
        result.put("code", 200);
        result.put("data", chapter);
        return result;
    }
}