package com.example.novelbackend.service;

import com.example.novelbackend.entity.Comment;
import java.util.List;
import java.util.Map;

public interface CommentService {

    // 获取章节评论列表（包含回复）
    Map<String, Object> getChapterComments(Long novelId, Integer chapterNum, Integer userId);

    // 添加评论
    Map<String, Object> addComment(Integer userId, Long novelId, Integer chapterNum,
                                   String content, Long parentId);

    // 删除评论
    Map<String, Object> deleteComment(Long commentId, Integer userId);

    // 点赞/取消点赞
    Map<String, Object> likeComment(Long commentId, Integer userId);

    // 获取评论总数
    int getCommentCount(Long novelId, Integer chapterNum);

    // 添加分页参数
    Map<String, Object> getChapterComments(Long novelId, Integer chapterNum, Integer userId,
                                           Integer page, Integer pageSize, String sortType);
}