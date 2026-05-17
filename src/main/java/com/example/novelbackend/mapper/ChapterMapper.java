package com.example.novelbackend.mapper;

import com.example.novelbackend.entity.Chapter;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import java.util.List;

@Mapper
public interface ChapterMapper {

    // 获取小说的所有章节
    List<Chapter> findByNovelId(@Param("novelId") Long novelId);

    // 获取章节详情
    Chapter findById(@Param("id") Long id);

    // 获取指定小说的指定章节（按章节序号）
    Chapter findByNovelIdAndChapterNum(@Param("novelId") Long novelId, @Param("chapterNum") Integer chapterNum);

    // 获取小说的章节总数
    int countByNovelId(@Param("novelId") Long novelId);

    // 获取上一章节
    Chapter getPrevChapter(@Param("novelId") Long novelId, @Param("chapterNum") Integer chapterNum);

    // 获取下一章节
    Chapter getNextChapter(@Param("novelId") Long novelId, @Param("chapterNum") Integer chapterNum);

    // 增加章节点击量
    int incrementViewCount(@Param("id") Long id);

    void insert(Chapter chapter);

    @Delete("DELETE FROM chapter WHERE novel_id = #{novelId}")
    int deleteByNovelId(@Param("novelId") Long novelId);

    // 更新章节
    @Update("UPDATE chapter SET title = #{title}, content = #{content}, word_count = #{wordCount} WHERE id = #{id}")
    int update(Chapter chapter);

    // 根据ID删除章节
    @Delete("DELETE FROM chapter WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}