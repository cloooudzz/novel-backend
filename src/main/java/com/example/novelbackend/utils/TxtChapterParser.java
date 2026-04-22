package com.example.novelbackend.utils;

import com.example.novelbackend.entity.Chapter;
import com.example.novelbackend.mapper.ChapterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Component
public class TxtChapterParser {

    @Autowired
    private ChapterMapper chapterMapper;

    private String readFileWithCorrectEncoding(File file) throws IOException {
        byte[] bytes = Files.readAllBytes(file.toPath());
        try {
            return new String(bytes, "GBK");
        } catch (Exception e) {
            try {
                return new String(bytes, "GB2312");
            } catch (Exception e2) {
                return new String(bytes, StandardCharsets.UTF_8);
            }
        }
    }

    public void parseAndSave(File txtFile, Long novelId) {
        try {
            String content = readFileWithCorrectEncoding(txtFile);
            String[] lines = content.split("\n");

            List<Chapter> chapters = new ArrayList<>();
            Chapter currentChapter = null;
            StringBuilder currentContent = new StringBuilder();

            for (String line : lines) {
                // 根据缩进判断行类型
                LineType lineType = detectLineTypeByIndent(line);

                if (lineType == LineType.TITLE) {
                    // 标题行：保存上一个章节，开始新章节
                    if (currentChapter != null) {
                        currentChapter.setContent(cleanContent(currentContent.toString()));
                        currentChapter.setWordCount(currentChapter.getContent().length());
                        chapters.add(currentChapter);
                    }

                    currentChapter = new Chapter();
                    currentChapter.setNovelId(novelId);
                    currentChapter.setTitle(extractTitleFromLine(line));
                    currentContent = new StringBuilder();

                } else if (lineType == LineType.BODY && currentChapter != null) {
                    // 正文行：追加内容
                    String bodyText = extractBodyFromLine(line);
                    if (!bodyText.isEmpty()) {
                        if (currentContent.length() > 0) {
                            currentContent.append("\n");
                        }
                        currentContent.append(bodyText);
                    }
                }
                // 空行和其他类型忽略
            }

            // 保存最后一个章节
            if (currentChapter != null) {
                currentChapter.setContent(cleanContent(currentContent.toString()));
                currentChapter.setWordCount(currentChapter.getContent().length());
                chapters.add(currentChapter);
            }


            // 导入数据库
            int successCount = 0;
            for (int i = 0; i < chapters.size(); i++) {
                Chapter chapter = chapters.get(i);
                chapter.setChapterNum(i + 1);  // 重新编号确保连续

                try {
                    chapterMapper.insert(chapter);
                    successCount++;
                    System.out.println("导入成功: " + chapter.getTitle() + " (字数: " + chapter.getWordCount() + ")");
                } catch (Exception e) {
                    System.err.println("插入失败: " + chapter.getTitle() + " - " + e.getMessage());
                }
            }

            System.out.println("\n========== 导入完成 ==========");
            System.out.println("成功导入 " + successCount + " 章，总计识别 " + chapters.size() + " 章");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("解析TXT失败: " + e.getMessage());
        }
    }

    /**
     * 根据缩进判断行类型
     * 第2列开始（0-3个空格）→ 标题行
     * 第5列开始（4个及以上空格）→ 正文行
     */
    private LineType detectLineTypeByIndent(String line) {
        if (line == null || line.isEmpty()) {
            return LineType.EMPTY;
        }

        // 获取去除空格后的内容
        String trimmed = line.trim();

        // 如果去除空格后为空，说明是空行，直接返回 EMPTY
        if (trimmed.isEmpty()) {
            return LineType.EMPTY;
        }

        // 计算前导空格数量
        int leadingSpaces = 0;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == ' ' || c == '\t') {
                leadingSpaces++;
            } else {
                break;
            }
        }

        // 第5列开始（4个或更多空格）
        if (leadingSpaces >= 4) {
            return LineType.BODY;
        }

        // 第2列开始（0-3个空格）
        if (leadingSpaces >= 0 && leadingSpaces <= 3) {
            return LineType.TITLE;
        }

        return LineType.EMPTY;
    }

    /**
     * 从标题行提取标题
     */
    private String extractTitleFromLine(String line) {
        // 移除前导空格
        String trimmed = line.trim();

        // 如果标题后有大量空格，截取到空格前
        int spaceIdx = trimmed.indexOf("  ");
        if (spaceIdx > 0) {
            trimmed = trimmed.substring(0, spaceIdx);
        }

        // 清理广告词
        trimmed = trimmed.replaceAll("手机端阅读请登陆.*?", "");
        trimmed = trimmed.replaceAll("16kxs\\.com", "");

        return trimmed.trim();
    }

    /**
     * 从正文行提取正文
     */
    private String extractBodyFromLine(String line) {
        // 移除前导空格
        String trimmed = line.trim();

        // 清理广告词
        trimmed = trimmed.replaceAll("手机端阅读请登陆.*?", "");
        trimmed = trimmed.replaceAll("16kxs\\.com", "");

        return trimmed;
    }

    /**
     * 清理正文内容
     */
    private String cleanContent(String content) {
        if (content == null || content.isEmpty()) {
            return "";
        }

        // 移除广告词
        content = content.replaceAll("手机端阅读请登陆.*?(\\s|$)", "");
        content = content.replaceAll("本书转载.*?网", "");
        content = content.replaceAll("w?w?w?\\..*?com", "");
        content = content.replaceAll("手机访问：.*?(\\s|$)", "");
        content = content.replaceAll("16k小说.*?整理", "");
        content = content.replaceAll("16kxs\\.com", "");
        content = content.replaceAll("一秒记住【..info】，为您提供精彩小说阅读。", "");
        content = content.replaceAll("<ahref=\"\"target=\"_nk\">http://.piaotian\"></a>", "");
        content = content.replaceAll("手机用户可访问wap..info观看小说，跟官网同步更新.", "");
        content = content.replaceAll("dengbidmxswqqxswyifan", "");
        content = content.replaceAll("shuyueepzwqqwxwxsguan", "");
        content = content.replaceAll("xs007zhuikereadw23zw", "");
        content = content.replaceAll("一秒记住【】，无弹窗，更新快，免费阅读！", "");
        content = content.replaceAll("《<b>玄鉴仙族</b>》笔下文学全文字更新,牢记网址:.33yqy", "");
        content = content.replaceAll("正在手打中，请稍等片刻，内容更新后，请重新刷新页面，即可获取最新更新！", "");

        // 修复段落格式：多个连续换行合并为两个
        content = content.replaceAll("\\n\\s*\\n\\s*\\n+", "\n\n");

        return content.trim();
    }




    /**
     * 行类型枚举
     */
    private enum LineType {
        TITLE,   // 标题行（第2列开始）
        BODY,    // 正文行（第5列开始）
        EMPTY    // 空行
    }
}