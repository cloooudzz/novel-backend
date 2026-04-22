package com.example.novelbackend.mapper;

import com.example.novelbackend.entity.Bookshelf;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface BookshelfMapper {

    // 添加书架
    int insert(Bookshelf bookshelf);

    // 根据用户ID和小说ID查询书架记录
    Bookshelf findByUserIdAndNovelId(@Param("userId") Integer userId, @Param("novelId") Long novelId);

    // 获取用户书架列表（包含小说信息）
    List<Map<String, Object>> findUserBookshelfWithNovelInfo(@Param("userId") Integer userId);

    // 更新最后阅读章节
    int updateLastReadChapter(@Param("id") Long id,
                              @Param("lastReadChapterNum") Integer lastReadChapterNum,
                              @Param("lastReadTime") java.time.LocalDateTime lastReadTime);

    // 删除书架记录
    int deleteByUserIdAndNovelId(@Param("userId") Integer userId, @Param("novelId") Long novelId);

    // 检查是否在书架中
    int countByUserIdAndNovelId(@Param("userId") Integer userId, @Param("novelId") Long novelId);
}