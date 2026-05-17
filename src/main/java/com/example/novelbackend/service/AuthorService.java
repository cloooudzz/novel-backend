package com.example.novelbackend.service;

import com.example.novelbackend.entity.Author;
import com.example.novelbackend.entity.Chapter;
import java.util.List;
import java.util.Map;

public interface AuthorService {

    // 申请成为作者
    Map<String, Object> applyForAuthor(Integer userId, String penName, String contact, String genre, String intro);

    // 检查用户是否是作者
    boolean isAuthor(Integer userId);

    // 获取作者信息
    Author getAuthorByUserId(Integer userId);

    // 获取作者的小说列表
    List<Map<String, Object>> getAuthorNovels(Integer userId);

    // 创建新小说
    Map<String, Object> createNovel(Integer userId, String title, String authorName,
                                    Integer categoryId, String cover, String intro, Integer status);

    // 更新小说信息
    Map<String, Object> updateNovel(Long novelId, Integer userId, String title,
                                    Integer categoryId, String cover, String intro, Integer status);

    // 删除小说
    Map<String, Object> deleteNovel(Long novelId, Integer userId);

    // 添加章节
    Map<String, Object> addChapter(Long novelId, Integer userId, Integer chapterNum,
                                   String title, String content);

    // 更新章节
    Map<String, Object> updateChapter(Long chapterId, Integer userId, String title, String content);

    // 删除章节
    Map<String, Object> deleteChapter(Long chapterId, Integer userId);

    // 获取小说的章节列表
    List<Chapter> getChaptersByNovel(Long novelId, Integer userId);

    // 获取章节详情
    Chapter getChapterDetail(Long chapterId, Integer userId);

    // 检查是否是小说作者
    boolean isNovelAuthor(Long novelId, Integer userId);
}