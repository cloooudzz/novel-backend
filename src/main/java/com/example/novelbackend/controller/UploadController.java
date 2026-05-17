package com.example.novelbackend.controller;

import cn.hutool.core.io.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Value("${cover.access.prefix:/covers/}")
    private String coverAccessPrefix;

    @PostMapping("/cover")
    public Map<String, Object> uploadCover(
            @RequestParam("file") MultipartFile file,
            @RequestParam("novelId") Long novelId) {
        Map<String, Object> result = new HashMap<>();

        if (file == null || file.isEmpty()) {
            result.put("code", 400);
            result.put("msg", "请上传封面文件");
            return result;
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            result.put("code", 400);
            result.put("msg", "文件大小不能超过 10MB");
            return result;
        }

        String originalFilename = file.getOriginalFilename();
        String ext = FileUtil.extName(originalFilename);

        if (!ext.matches("(?i)jpg|png|gif|jpeg|webp")) {
            result.put("code", 400);
            result.put("msg", "仅支持JPG/PNG/GIF/WEBP格式");
            return result;
        }

        // 使用 novelId 作为文件名
        String fileName = novelId + "." + ext;

        // 动态获取项目根目录
        String projectPath = System.getProperty("user.dir");
        String uploadPath = projectPath + "/uploads/covers/";
        FileUtil.mkdir(uploadPath);

        File destFile = new File(uploadPath, fileName);

        try {
            file.transferTo(destFile);
            String coverPath = coverAccessPrefix + fileName;

            result.put("code", 200);
            result.put("msg", "上传成功");
            Map<String, String> data = new HashMap<>();
            data.put("coverUrl", coverPath);
            result.put("data", data);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("msg", "封面上传失败：" + e.getMessage());
        }
        return result;
    }
}