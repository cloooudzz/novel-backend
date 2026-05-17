package com.example.novelbackend.service.impl;

import com.example.novelbackend.entity.Author;
import com.example.novelbackend.entity.Chapter;
import com.example.novelbackend.entity.Novel;
import com.example.novelbackend.mapper.AuthorMapper;
import com.example.novelbackend.mapper.ChapterMapper;
import com.example.novelbackend.mapper.NovelMapper;
import com.example.novelbackend.service.AuthorService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthorServiceImpl implements AuthorService {

    @Resource
    private AuthorMapper authorMapper;

    @Resource
    private NovelMapper novelMapper;

    @Resource
    private ChapterMapper chapterMapper;

    @Override
    @Transactional
    public Map<String, Object> applyForAuthor(Integer userId, String penName, String contact, String genre, String intro) {
        Map<String, Object> result = new HashMap<>();

        if (authorMapper.countByUserId(userId) > 0) {
            result.put("success", false);
            result.put("msg", "您已经是作者了");
            return result;
        }

        Author existing = authorMapper.findByPenName(penName);
        if (existing != null) {
            result.put("success", false);
            result.put("msg", "该笔名已被使用");
            return result;
        }

        Author author = new Author();
        author.setUserId(userId);
        author.setPenName(penName);
        author.setContact(contact);
        author.setGenre(genre);
        author.setIntro(intro);
        author.setStatus(1);
        author.setCreateTime(LocalDateTime.now());
        author.setUpdateTime(LocalDateTime.now());

        authorMapper.insert(author);

        result.put("success", true);
        result.put("msg", "申请成功，您已成为作者");
        result.put("penName", penName);
        return result;
    }

    @Override
    public boolean isAuthor(Integer userId) {
        if (userId == null) return false;
        return authorMapper.countByUserId(userId) > 0;
    }

    @Override
    public Author getAuthorByUserId(Integer userId) {
        return authorMapper.findByUserId(userId);
    }

    @Override
    public List<Map<String, Object>> getAuthorNovels(Integer userId) {
        Author author = authorMapper.findByUserId(userId);
        if (author == null) {
            return new ArrayList<>();
        }

        List<Map<String, Object>> novels = novelMapper.findByAuthorPenName(author.getPenName());

        // 确保每个小说都有 categoryId 字段（如果数据库返回的是 category_id，这里做转换）
        for (Map<String, Object> novel : novels) {
            // 如果 categoryId 不存在但有 category_id，则添加 categoryId
            if (!novel.containsKey("categoryId") && novel.containsKey("category_id")) {
                novel.put("categoryId", novel.get("category_id"));
            }
            // 确保状态字段是整数
            if (novel.get("status") instanceof String) {
                novel.put("status", Integer.parseInt((String) novel.get("status")));
            }
        }

        return novels;
    }

    @Override
    @Transactional
    public Map<String, Object> createNovel(Integer userId, String title, String authorName,
                                           Integer categoryId, String cover, String intro, Integer status) {
        Map<String, Object> result = new HashMap<>();

        if (!isAuthor(userId)) {
            result.put("success", false);
            result.put("msg", "您还不是作者，请先申请成为作者");
            return result;
        }

        Author author = getAuthorByUserId(userId);
        String penName = author != null ? author.getPenName() : authorName;

        Novel novel = new Novel();
        novel.setTitle(title);
        novel.setAuthor(penName);
        novel.setCategoryId(categoryId);
        novel.setCover(cover);
        novel.setIntro(intro);
        novel.setStatus(status != null ? status : 1);
        novel.setViewCount(0);
        novel.setCollectCount(0);
        novel.setIsRecommend(0);
        novel.setIsHot(0);
        novel.setCreateTime(LocalDateTime.now());
        novel.setUpdateTime(LocalDateTime.now());

        novelMapper.insert(novel);

        result.put("success", true);
        result.put("msg", "小说创建成功");
        result.put("novelId", novel.getId());
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> updateNovel(Long novelId, Integer userId, String title,
                                           Integer categoryId, String cover, String intro, Integer status) {
        Map<String, Object> result = new HashMap<>();

        if (!isNovelAuthor(novelId, userId)) {
            result.put("success", false);
            result.put("msg", "无权操作该小说");
            return result;
        }

        Novel novel = novelMapper.findById(novelId);
        if (novel == null) {
            result.put("success", false);
            result.put("msg", "小说不存在");
            return result;
        }

        if (title != null) novel.setTitle(title);
        if (categoryId != null) novel.setCategoryId(categoryId);
        if (cover != null) novel.setCover(cover);
        if (intro != null) novel.setIntro(intro);
        if (status != null) novel.setStatus(status);
        novel.setUpdateTime(LocalDateTime.now());

        novelMapper.update(novel);

        result.put("success", true);
        result.put("msg", "小说更新成功");
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> deleteNovel(Long novelId, Integer userId) {
        Map<String, Object> result = new HashMap<>();

        if (!isNovelAuthor(novelId, userId)) {
            result.put("success", false);
            result.put("msg", "无权操作该小说");
            return result;
        }

        int chapterCount = chapterMapper.countByNovelId(novelId);
        if (chapterCount > 0) {
            result.put("success", false);
            result.put("msg", "请先删除所有章节后再删除小说");
            return result;
        }

        novelMapper.deleteById(novelId);

        result.put("success", true);
        result.put("msg", "小说删除成功");
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> addChapter(Long novelId, Integer userId, Integer chapterNum,
                                          String title, String content) {
        Map<String, Object> result = new HashMap<>();

        if (!isNovelAuthor(novelId, userId)) {
            result.put("success", false);
            result.put("msg", "无权操作该小说");
            return result;
        }

        Chapter existing = chapterMapper.findByNovelIdAndChapterNum(novelId, chapterNum);
        if (existing != null) {
            result.put("success", false);
            result.put("msg", "该章节号已存在");
            return result;
        }

        Chapter chapter = new Chapter();
        chapter.setNovelId(novelId);
        chapter.setChapterNum(chapterNum);
        chapter.setTitle(title);
        chapter.setContent(content != null ? content : "");
        chapter.setWordCount(content != null ? content.length() : 0);
        chapter.setViewCount(0);
        chapter.setCreateTime(LocalDateTime.now());

        chapterMapper.insert(chapter);
        novelMapper.updateUpdateTime(novelId);

        result.put("success", true);
        result.put("msg", "章节添加成功");
        result.put("chapterId", chapter.getId());
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> updateChapter(Long chapterId, Integer userId, String title, String content) {
        Map<String, Object> result = new HashMap<>();

        Chapter chapter = chapterMapper.findById(chapterId);
        if (chapter == null) {
            result.put("success", false);
            result.put("msg", "章节不存在");
            return result;
        }

        if (!isNovelAuthor(chapter.getNovelId(), userId)) {
            result.put("success", false);
            result.put("msg", "无权操作该章节");
            return result;
        }

        if (title != null) chapter.setTitle(title);
        if (content != null) {
            chapter.setContent(content);
            chapter.setWordCount(content.length());
        }

        chapterMapper.update(chapter);
        novelMapper.updateUpdateTime(chapter.getNovelId());

        result.put("success", true);
        result.put("msg", "章节更新成功");
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> deleteChapter(Long chapterId, Integer userId) {
        Map<String, Object> result = new HashMap<>();

        Chapter chapter = chapterMapper.findById(chapterId);
        if (chapter == null) {
            result.put("success", false);
            result.put("msg", "章节不存在");
            return result;
        }

        if (!isNovelAuthor(chapter.getNovelId(), userId)) {
            result.put("success", false);
            result.put("msg", "无权操作该章节");
            return result;
        }

        chapterMapper.deleteById(chapterId);
        novelMapper.updateUpdateTime(chapter.getNovelId());

        result.put("success", true);
        result.put("msg", "章节删除成功");
        return result;
    }

    @Override
    public List<Chapter> getChaptersByNovel(Long novelId, Integer userId) {
        if (!isNovelAuthor(novelId, userId)) {
            return null;
        }
        return chapterMapper.findByNovelId(novelId);
    }

    @Override
    public Chapter getChapterDetail(Long chapterId, Integer userId) {
        Chapter chapter = chapterMapper.findById(chapterId);
        if (chapter == null) return null;

        if (!isNovelAuthor(chapter.getNovelId(), userId)) {
            return null;
        }
        return chapter;
    }

    @Override
    public boolean isNovelAuthor(Long novelId, Integer userId) {
        if (userId == null) return false;
        Novel novel = novelMapper.findById(novelId);
        if (novel == null) return false;

        Author author = authorMapper.findByUserId(userId);
        if (author == null) return false;

        return novel.getAuthor().equals(author.getPenName());
    }
}