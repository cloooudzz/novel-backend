package com.example.novelbackend.mapper;

import com.example.novelbackend.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface CommentMapper {

    // 获取章节的评论列表（顶层评论）
    List<Comment> findByChapter(@Param("novelId") Long novelId,
                                @Param("chapterNum") Integer chapterNum,
                                @Param("userId") Integer userId);

    // 获取评论的回复列表
    List<Comment> findRepliesByParentId(@Param("parentId") Long parentId,
                                        @Param("userId") Integer userId);

    // 添加评论
    int insert(Comment comment);

    // 删除评论（软删除）
    int deleteById(@Param("id") Long id, @Param("userId") Integer userId);

    // 增加评论点赞数
    int incrementLikeCount(@Param("id") Long id);

    // 减少评论点赞数
    int decrementLikeCount(@Param("id") Long id);

    // 增加回复数
    int incrementReplyCount(@Param("id") Long id);

    // 检查评论是否属于用户
    int checkCommentOwner(@Param("id") Long id, @Param("userId") Integer userId);

    // 获取评论详情
    Comment findById(@Param("id") Long id);

    // 获取章节评论总数
    int countByChapter(@Param("novelId") Long novelId, @Param("chapterNum") Integer chapterNum);

    // ========== 新增方法（分页排序） ==========

    /**
     * 按时间倒序获取章节评论列表（最新优先）
     * @param novelId 小说ID
     * @param chapterNum 章节号
     * @param userId 当前用户ID（用于判断是否点赞）
     * @param offset 偏移量
     * @param pageSize 每页数量
     * @return 评论列表
     */
    List<Comment> findByChapterOrderByTime(@Param("novelId") Long novelId,
                                           @Param("chapterNum") Integer chapterNum,
                                           @Param("userId") Integer userId,
                                           @Param("offset") int offset,
                                           @Param("pageSize") int pageSize);

    /**
     * 按点赞数倒序获取章节评论列表（最热优先）
     * @param novelId 小说ID
     * @param chapterNum 章节号
     * @param userId 当前用户ID（用于判断是否点赞）
     * @param offset 偏移量
     * @param pageSize 每页数量
     * @return 评论列表
     */
    List<Comment> findByChapterOrderByLike(@Param("novelId") Long novelId,
                                           @Param("chapterNum") Integer chapterNum,
                                           @Param("userId") Integer userId,
                                           @Param("offset") int offset,
                                           @Param("pageSize") int pageSize);
}