package com.example.novelbackend.service;

public interface UserService {
    // 注册
    String register(String account, String username, String password);

    // 登录（使用账号）
    String login(String account, String password);

    // 修改密码
    String changePassword(Integer userId, String oldPassword, String newPassword);

    // 修改头像
    String changeAvatar(Integer userId, String avatarPath);

    // 修改用户名
    String changeUsername(Integer userId, String newUsername);
}