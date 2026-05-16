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
    public void importQiuMo() {
        File file = new File("E:/桌面/小说/求魔.txt");
        Long novelId = 29L;
        parser.parseAndSave(file, novelId);
    }

    @Test
    public void importQuanQiuGaoWu() {
        File file = new File("E:/桌面/小说/全球高武.txt");
        Long novelId = 30L;
        parser.parseAndSave(file, novelId);
    }

    @Test
    public void importShengXu() {
        File file = new File("E:/桌面/小说/圣墟.txt");
        Long novelId = 31L;
        parser.parseAndSave(file, novelId);
    }

    @Test
    public void importTianDaoTuShuGuan() {
        File file = new File("E:/桌面/小说/天道图书馆.txt");
        Long novelId = 32L;
        parser.parseAndSave(file, novelId);
    }

    @Test
    public void importWanMeiShiJie() {
        File file = new File("E:/桌面/小说/完美世界.txt");
        Long novelId = 33L;
        parser.parseAndSave(file, novelId);
    }

    @Test
    public void importXianNi() {
        File file = new File("E:/桌面/小说/仙逆.txt");
        Long novelId = 34L;
        parser.parseAndSave(file, novelId);
    }

    @Test
    public void importXingChenBian() {
        File file = new File("E:/桌面/小说/星辰变.txt");
        Long novelId = 35L;
        parser.parseAndSave(file, novelId);
    }

    @Test
    public void importYeDeMingMingShu() {
        File file = new File("E:/桌面/小说/夜的命名术.txt");
        Long novelId = 36L;
        parser.parseAndSave(file, novelId);
    }

    @Test
    public void importYiNianYongHeng() {
        File file = new File("E:/桌面/小说/一念永恒.txt");
        Long novelId = 37L;
        parser.parseAndSave(file, novelId);
    }

    @Test
    public void importZheTian() {
        File file = new File("E:/桌面/小说/遮天.txt");
        Long novelId = 38L;
        parser.parseAndSave(file, novelId);
    }

    @Test
    public void importWanZuZhiJie() {
        File file = new File("E:/桌面/小说/万族之劫.txt");
        Long novelId = 39L;
        parser.parseAndSave(file, novelId);
    }
}