package com.example.novelbackend;

import com.example.novelbackend.utils.TxtChapterParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

@SpringBootTest
public class ImportNovelTest {

    @Autowired
    private TxtChapterParser parser;

    @Test
    public void importDouPoCangQiong() {
        File file = new File("E:/桌面/新建文件夹/吞噬星空.txt");
        Long novelId = 22L;
        parser.parseAndSave(file, novelId);
    }
}