package com.example.novelbackend.service.impl;

import com.example.novelbackend.entity.Comment;
import com.example.novelbackend.mapper.CommentLikeMapper;
import com.example.novelbackend.mapper.CommentMapper;
import com.example.novelbackend.service.CommentService;
import com.example.novelbackend.service.RecommendationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private CommentLikeMapper commentLikeMapper;

    @Resource
    private RecommendationService recommendationService;

    @Override
    public Map<String, Object> getChapterComments(Long novelId, Integer chapterNum, Integer userId) {
        Map<String, Object> result = new HashMap<>();

        // 获取顶层评论
        List<Comment> comments = commentMapper.findByChapter(novelId, chapterNum, userId);

        // 为每个评论加载回复
        for (Comment comment : comments) {
            List<Comment> replies = commentMapper.findRepliesByParentId(comment.getId(), userId);
            comment.setReplyCount(replies.size());
            result.put("replies_" + comment.getId(), replies);
        }

        result.put("comments", comments);
        result.put("total", commentMapper.countByChapter(novelId, chapterNum));

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> addComment(Integer userId, Long novelId, Integer chapterNum,
                                          String content, Long parentId) {
        Map<String, Object> result = new HashMap<>();

        if (content == null || content.trim().isEmpty()) {
            result.put("code", 400);
            result.put("msg", "评论内容不能为空");
            return result;
        }

        if (content.length() > 500) {
            result.put("code", 400);
            result.put("msg", "评论内容不能超过500字");
            return result;
        }

        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setNovelId(novelId);
        comment.setChapterNum(chapterNum);
        comment.setContent(content.trim());
        comment.setParentId(parentId);

        int rows = commentMapper.insert(comment);

        if (rows > 0) {
            // 如果是回复，增加父评论的回复数
            if (parentId != null) {
                commentMapper.incrementReplyCount(parentId);
            }

            // 获取用户信息
            Comment newComment = commentMapper.findById(comment.getId());

            result.put("code", 200);
            result.put("msg", "评论成功");
            result.put("data", newComment);

            //记录行为
            recommendationService.recordUserBehavior(userId, novelId, "comment");
        } else {
            result.put("code", 500);
            result.put("msg", "评论失败");
        }

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> deleteComment(Long commentId, Integer userId) {
        Map<String, Object> result = new HashMap<>();

        int rows = commentMapper.deleteById(commentId, userId);

        if (rows > 0) {
            result.put("code", 200);
            result.put("msg", "删除成功");
        } else {
            result.put("code", 400);
            result.put("msg", "删除失败，您没有权限删除此评论");
        }

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> likeComment(Long commentId, Integer userId) {
        Map<String, Object> result = new HashMap<>();

        // 检查是否已点赞
        int liked = commentLikeMapper.checkLiked(commentId, userId);

        if (liked > 0) {
            // 取消点赞
            commentLikeMapper.delete(commentId, userId);
            commentMapper.decrementLikeCount(commentId);
            result.put("code", 200);
            result.put("msg", "取消点赞");
            result.put("liked", false);
        } else {
            // 添加点赞
            commentLikeMapper.insert(commentId, userId);
            commentMapper.incrementLikeCount(commentId);
            result.put("code", 200);
            result.put("msg", "点赞成功");
            result.put("liked", true);
        }

        return result;
    }

    @Override
    public int getCommentCount(Long novelId, Integer chapterNum) {
        return commentMapper.countByChapter(novelId, chapterNum);
    }


    @Override
    public Map<String, Object> getChapterComments(Long novelId, Integer chapterNum, Integer userId,
                                                  Integer page, Integer pageSize, String sortType) {
        Map<String, Object> result = new HashMap<>();

        // 计算偏移量
        int offset = (page - 1) * pageSize;

        // 根据排序类型获取评论
        List<Comment> comments;
        if ("latest".equals(sortType)) {
            comments = commentMapper.findByChapterOrderByTime(novelId, chapterNum, userId, offset, pageSize);
        } else {
            comments = commentMapper.findByChapterOrderByLike(novelId, chapterNum, userId, offset, pageSize);
        }

        // 获取总数
        int total = commentMapper.countByChapter(novelId, chapterNum);

        // 为每个评论加载回复
        for (Comment comment : comments) {
            List<Comment> replies = commentMapper.findRepliesByParentId(comment.getId(), userId);
            comment.setReplyCount(replies.size());
            result.put("replies_" + comment.getId(), replies);
        }

        result.put("comments", comments);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);

        return result;
    }
}