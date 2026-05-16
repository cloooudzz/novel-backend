package com.example.novelbackend.service.impl;

import com.example.novelbackend.entity.Bookshelf;
import com.example.novelbackend.mapper.BookshelfMapper;
import com.example.novelbackend.mapper.ChapterMapper;
import com.example.novelbackend.service.BookshelfService;
import com.example.novelbackend.service.RecommendationService;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookshelfServiceImpl implements BookshelfService {

    @Resource
    private BookshelfMapper bookshelfMapper;

    @Resource
    private RecommendationService recommendationService;

    @Resource
    private ChapterMapper chapterMapper;


    @Override
    public Map<String, Object> addToBookshelf(Integer userId, Long novelId) {
        Map<String, Object> result = new HashMap<>();

        // 检查是否已存在
        Bookshelf exist = bookshelfMapper.findByUserIdAndNovelId(userId, novelId);
        if (exist != null) {
            result.put("success", false);
            result.put("msg", "小说已在书架中");
            return result;
        }

        // 添加到书架
        Bookshelf bookshelf = new Bookshelf();
        bookshelf.setUserId(userId);
        bookshelf.setNovelId(novelId);
        bookshelf.setCreateTime(LocalDateTime.now());

        int rows = bookshelfMapper.insert(bookshelf);
        if (rows > 0) {
            result.put("success", true);
            result.put("msg", "已加入书架");
            //记录行为
            recommendationService.recordUserBehavior(userId, novelId, "add_to_shelf");
        } else {
            result.put("success", false);
            result.put("msg", "加入书架失败");
        }

        return result;
    }

    @Override
    public Map<String, Object> removeFromBookshelf(Integer userId, Long novelId) {
        Map<String, Object> result = new HashMap<>();

        int rows = bookshelfMapper.deleteByUserIdAndNovelId(userId, novelId);
        if (rows > 0) {
            result.put("success", true);
            result.put("msg", "已从书架移除");
        } else {
            result.put("success", false);
            result.put("msg", "移除失败，书架中不存在该小说");
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getUserBookshelf(Integer userId) {
        return bookshelfMapper.findUserBookshelfWithNovelInfo(userId);
    }

    @Override
    public void updateReadProgress(Integer userId, Long novelId, Integer chapterNum) {
        Bookshelf bookshelf = bookshelfMapper.findByUserIdAndNovelId(userId, novelId);
        if (bookshelf != null) {
            bookshelfMapper.updateLastReadChapter(
                    bookshelf.getId(),
                    chapterNum,
                    LocalDateTime.now()
            );
        }
    }

    @Override
    public boolean isInBookshelf(Integer userId, Long novelId) {
        return bookshelfMapper.countByUserIdAndNovelId(userId, novelId) > 0;
    }
}