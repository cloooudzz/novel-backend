package com.example.novelbackend.service;

import com.example.novelbackend.entity.User;

public interface UserService {
    // 注册
    String register(String username, String password);

    // 登录
    String login(String username, String password);

    // 新增：修改密码（参数：用户ID、旧密码、新密码）
    String changePassword(Integer userId, String oldPassword, String newPassword);

    // 新增：修改头像（参数：用户ID、头像路径）
    String changeAvatar(Integer userId, String avatarPath);

    // 新增：修改用户名
    String changeUsername(Integer userId, String newUsername);
}