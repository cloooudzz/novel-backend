package com.example.novelbackend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommentLikeMapper {

    // 添加点赞记录
    int insert(@Param("commentId") Long commentId, @Param("userId") Integer userId);

    // 删除点赞记录
    int delete(@Param("commentId") Long commentId, @Param("userId") Integer userId);

    // 检查是否已点赞
    int checkLiked(@Param("commentId") Long commentId, @Param("userId") Integer userId);
}