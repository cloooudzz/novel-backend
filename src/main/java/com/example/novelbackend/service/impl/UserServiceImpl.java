package com.example.novelbackend.service.impl;

import com.example.novelbackend.entity.User;
import com.example.novelbackend.mapper.UserMapper;
import com.example.novelbackend.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    // 密码加密器
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();



    @Override
    public String register(String username, String password) {
        // 1. 检查用户名是否已存在
        User existUser = userMapper.findByUsername(username);
        if (existUser != null) {
            return "用户名已存在";
        }

        // 2. 封装用户信息，插入数据库
        User user = new User();
        user.setUsername(username);
        user.setPassword(password); // 真实项目建议加密，这里先明文演示
        user.setCreateTime(LocalDateTime.now());
        userMapper.insert(user);

        return "success";
    }

    @Override
    public String login(String username, String password) {
        // 1. 根据用户名查询用户
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return "用户名不存在";
        }

        // 2. 校验密码
        if (!user.getPassword().equals(password)) {
            return "密码错误";
        }

        return "success";
    }


    // 新增：修改密码核心逻辑
    @Override
    public String changePassword(Integer userId, String oldPassword, String newPassword) {
        // 1. 校验参数
        if (oldPassword == null || newPassword == null || newPassword.length() < 6) {
            return "新密码不能为空且长度不少于6位";
        }

        // 2. 根据ID查用户
        User user = userMapper.findById(userId);
        if (user == null) {
            return "用户不存在";
        }

        // 3. 验证旧密码（对比加密后的密码）
        if (!oldPassword.equals(user.getPassword())) {
            return "旧密码错误";
        }

        // 4. 直接更新新密码（明文）
        int rows = userMapper.updatePassword(userId, newPassword);
        return rows > 0 ? "密码修改成功" : "密码修改失败";
    }

    // 新增：修改头像核心逻辑
    @Override
    public String changeAvatar(Integer userId, String avatarPath) {
        // 1. 校验参数
        if (avatarPath == null || avatarPath.isEmpty()) {
            return "头像路径不能为空";
        }

        // 2. 更新头像
        int rows = userMapper.updateAvatar(userId, avatarPath);
        return rows > 0 ? "头像修改成功" : "头像修改失败";
    }

    // 新增：修改用户名核心逻辑
    @Override
    public String changeUsername(Integer userId, String newUsername) {
        // 1. 校验参数
        if (newUsername == null || newUsername.trim().isEmpty()) {
            return "用户名不能为空";
        }
        if (newUsername.length() < 2 || newUsername.length() > 20) {
            return "用户名长度应为2-20个字符";
        }

        // 2. 根据ID查用户
        User user = userMapper.findById(userId);
        if (user == null) {
            return "用户不存在";
        }

        // 3. 检查新用户名是否已被其他用户使用
        User existUser = userMapper.findByUsername(newUsername);
        if (existUser != null && !existUser.getId().equals(userId.longValue())) {
            return "用户名已被占用";
        }

        // 4. 更新用户名
        int rows = userMapper.updateUsername(userId, newUsername);
        return rows > 0 ? "用户名修改成功" : "用户名修改失败";
    }
}