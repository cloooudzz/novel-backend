package com.example.novelbackend.mapper;

import com.example.novelbackend.entity.Novel;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

@Mapper
public interface NovelMapper {

    // 获取所有小说（书城用）
    List<Novel> findAll();

    // 根据ID获取小说详情
    Novel findById(@Param("id") Long id);

    // 获取热门推荐小说
    List<Novel> findHotNovels(@Param("limit") int limit);

    // 获取推荐小说
    List<Novel> findRecommendNovels(@Param("limit") int limit);

    // 获取排行榜（按点击量）
    List<Novel> findRankByView(@Param("limit") int limit);

    // 按分类获取小说
    List<Novel> findByCategory(@Param("categoryId") Integer categoryId);

    // 增加点击量
    int incrementViewCount(@Param("id") Long id);

    // 搜索小说
    List<Novel> search(@Param("keyword") String keyword);

    // 获取小说总章节数
    int getChapterCount(@Param("novelId") Long novelId);

    // 获取最新章节标题
    String getLastChapterTitle(@Param("novelId") Long novelId);

    // 根据作者笔名获取小说列表
    @Select("SELECT n.id, n.title, n.author, n.category_id as categoryId, n.cover, n.intro, " +
            "n.status, n.view_count as viewCount, n.collect_count as collectCount, " +
            "n.is_recommend as isRecommend, n.is_hot as isHot, " +
            "n.create_time as createTime, n.update_time as updateTime, " +
            "c.name as categoryName, " +
            "(SELECT COUNT(*) FROM chapter WHERE novel_id = n.id) as totalChapters " +
            "FROM novel n LEFT JOIN category c ON n.category_id = c.id " +
            "WHERE n.author = #{penName} ORDER BY n.update_time DESC")
    List<Map<String, Object>> findByAuthorPenName(@Param("penName") String penName);

    // 插入小说
    @Insert("INSERT INTO novel (title, author, category_id, cover, intro, status, " +
            "view_count, collect_count, is_recommend, is_hot, create_time, update_time) " +
            "VALUES (#{title}, #{author}, #{categoryId}, #{cover}, #{intro}, #{status}, " +
            "#{viewCount}, #{collectCount}, #{isRecommend}, #{isHot}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Novel novel);

    // 更新小说
    @Update("UPDATE novel SET title = #{title}, category_id = #{categoryId}, " +
            "cover = #{cover}, intro = #{intro}, status = #{status}, update_time = #{updateTime} " +
            "WHERE id = #{id}")
    int update(Novel novel);

    // 删除小说
    @Delete("DELETE FROM novel WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    // 更新更新时间
    @Update("UPDATE novel SET update_time = NOW() WHERE id = #{novelId}")
    int updateUpdateTime(@Param("novelId") Long novelId);
}