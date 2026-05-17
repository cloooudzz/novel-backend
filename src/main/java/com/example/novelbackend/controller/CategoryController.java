package com.example.novelbackend.controller;

import com.example.novelbackend.entity.Category;
import com.example.novelbackend.mapper.CategoryMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryMapper categoryMapper;

    // 获取所有分类
    @GetMapping("/list")
    public Map<String, Object> getCategoryList() {
        Map<String, Object> result = new HashMap<>();
        List<Category> list = categoryMapper.findAll();
        result.put("code", 200);
        result.put("data", list);
        return result;
    }
}