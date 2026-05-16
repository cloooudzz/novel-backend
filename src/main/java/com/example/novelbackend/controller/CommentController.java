package com.example.novelbackend.controller;

import com.example.novelbackend.service.CommentService;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private CommentService commentService;

    // 获取章节评论列表（带分页和排序）
    @GetMapping("/list")
    public Map<String, Object> getComments(
            @RequestParam Long novelId,
            @RequestParam Integer chapterNum,
            @RequestParam(required = false) Integer userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "hot") String sortType) {
        return commentService.getChapterComments(novelId, chapterNum, userId, page, pageSize, sortType);
    }

    // 添加评论
    @PostMapping("/add")
    public Map<String, Object> addComment(@RequestBody Map<String, Object> params) {
        Integer userId = (Integer) params.get("userId");
        Long novelId = ((Number) params.get("novelId")).longValue();
        Integer chapterNum = (Integer) params.get("chapterNum");
        String content = (String) params.get("content");
        Long parentId = params.get("parentId") != null ?
                ((Number) params.get("parentId")).longValue() : null;

        if (userId == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 401);
            result.put("msg", "请先登录");
            return result;
        }

        return commentService.addComment(userId, novelId, chapterNum, content, parentId);
    }

    // 删除评论
    @DeleteMapping("/delete")
    public Map<String, Object> deleteComment(
            @RequestParam Long commentId,
            @RequestParam Integer userId) {
        return commentService.deleteComment(commentId, userId);
    }

    // 点赞/取消点赞
    @PostMapping("/like")
    public Map<String, Object> likeComment(@RequestBody Map<String, Object> params) {
        Long commentId = ((Number) params.get("commentId")).longValue();
        Integer userId = (Integer) params.get("userId");

        if (userId == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 401);
            result.put("msg", "请先登录");
            return result;
        }

        return commentService.likeComment(commentId, userId);
    }

    // 获取评论总数
    @GetMapping("/count")
    public Map<String, Object> getCommentCount(
            @RequestParam Long novelId,
            @RequestParam Integer chapterNum) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("count", commentService.getCommentCount(novelId, chapterNum));
        return result;
    }
}