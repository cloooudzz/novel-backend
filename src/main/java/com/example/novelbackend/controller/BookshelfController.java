package com.example.novelbackend.controller;

import com.example.novelbackend.service.BookshelfService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/bookshelf")
public class BookshelfController {

    @Resource
    private BookshelfService bookshelfService;

    private Integer getUserIdFromRequest(HttpServletRequest request) {
        return (Integer) request.getAttribute("userId");
    }

    // 添加到书架
    @PostMapping("/add")
    public Map<String, Object> addToBookshelf(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();

        Integer userId = getUserIdFromRequest(request);
        if (userId == null) {
            result.put("code", 401);
            result.put("msg", "请先登录");
            return result;
        }

        Long novelId = ((Number) params.get("novelId")).longValue();

        if (novelId == null) {
            result.put("code", 400);
            result.put("msg", "参数错误");
            return result;
        }

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

    // 从书架移除
    @DeleteMapping("/remove")
    public Map<String, Object> removeFromBookshelf(HttpServletRequest request, @RequestParam Long novelId) {
        Map<String, Object> result = new HashMap<>();

        Integer userId = getUserIdFromRequest(request);
        if (userId == null) {
            result.put("code", 401);
            result.put("msg", "请先登录");
            return result;
        }

        Map<String, Object> serviceResult = bookshelfService.removeFromBookshelf(userId, novelId);

        if ((Boolean) serviceResult.get("success")) {
            result.put("code", 200);
            result.put("msg", serviceResult.get("msg"));
        } else {
            result.put("code", 400);
            result.put("msg", serviceResult.get("msg"));
        }

        return result;
    }

    // 获取用户书架列表
    @GetMapping("/list")
    public Map<String, Object> getUserBookshelf(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();

        Integer userId = getUserIdFromRequest(request);
        if (userId == null) {
            result.put("code", 401);
            result.put("msg", "请先登录");
            return result;
        }

        List<Map<String, Object>> list = bookshelfService.getUserBookshelf(userId);

        result.put("code", 200);
        result.put("data", list);
        result.put("total", list.size());

        return result;
    }

    // 检查是否在书架中
    @GetMapping("/check")
    public Map<String, Object> checkInBookshelf(HttpServletRequest request, @RequestParam Long novelId) {
        Map<String, Object> result = new HashMap<>();

        Integer userId = getUserIdFromRequest(request);
        if (userId == null) {
            result.put("code", 401);
            result.put("msg", "请先登录");
            return result;
        }

        boolean inBookshelf = bookshelfService.isInBookshelf(userId, novelId);

        result.put("code", 200);
        result.put("inBookshelf", inBookshelf);

        return result;
    }

    // 更新阅读进度
    @PostMapping("/progress")
    public Map<String, Object> updateReadProgress(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();

        Integer userId = getUserIdFromRequest(request);
        if (userId == null) {
            result.put("code", 401);
            result.put("msg", "请先登录");
            return result;
        }

        Long novelId = ((Number) params.get("novelId")).longValue();
        Integer chapterNum = (Integer) params.get("chapterNum");

        if (novelId == null || chapterNum == null) {
            result.put("code", 400);
            result.put("msg", "参数错误");
            return result;
        }

        bookshelfService.updateReadProgress(userId, novelId, chapterNum);

        result.put("code", 200);
        result.put("msg", "阅读进度已更新");

        return result;
    }
}