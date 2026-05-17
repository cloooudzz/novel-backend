package com.example.novelbackend.service.impl;

import com.example.novelbackend.entity.User;
import com.example.novelbackend.mapper.UserMapper;
import com.example.novelbackend.service.UserService;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public String register(String account, String username, String password) {
        // 1. 检查账号是否已存在
        User existByAccount = userMapper.findByAccount(account);
        if (existByAccount != null) {
            return "账号已存在";
        }

        // 2. 检查用户名（昵称）是否已存在
        User existByUsername = userMapper.findByUsername(username);
        if (existByUsername != null) {
            return "用户名已被使用";
        }

        // 3. 校验参数
        if (account == null || account.trim().isEmpty()) {
            return "账号不能为空";
        }
        if (username == null || username.trim().isEmpty()) {
            return "用户名不能为空";
        }
        if (password == null || password.length() < 6) {
            return "密码长度不能少于6位";
        }

        // 4. 注册新用户
        User user = new User();
        user.setAccount(account);
        user.setUsername(username);
        user.setPassword(password);
        user.setCreateTime(LocalDateTime.now());

        int rows = userMapper.insert(user);
        return rows > 0 ? "success" : "注册失败";
    }

    @Override
    public String login(String account, String password) {
        // 1. 根据账号查询用户
        User user = userMapper.findByAccount(account);
        if (user == null) {
            return "账号不存在";
        }

        // 2. 校验密码
        if (!user.getPassword().equals(password)) {
            return "密码错误";
        }

        return "success";
    }

    @Override
    public String changePassword(Integer userId, String oldPassword, String newPassword) {
        if (oldPassword == null || newPassword == null || newPassword.length() < 6) {
            return "新密码不能为空且长度不少于6位";
        }

        User user = userMapper.findById(userId);
        if (user == null) {
            return "用户不存在";
        }

        if (!oldPassword.equals(user.getPassword())) {
            return "旧密码错误";
        }

        int rows = userMapper.updatePassword(userId, newPassword);
        return rows > 0 ? "密码修改成功" : "密码修改失败";
    }

    @Override
    public String changeAvatar(Integer userId, String avatarPath) {
        if (avatarPath == null || avatarPath.isEmpty()) {
            return "头像路径不能为空";
        }

        int rows = userMapper.updateAvatar(userId, avatarPath);
        return rows > 0 ? "头像修改成功" : "头像修改失败";
    }

    @Override
    public String changeUsername(Integer userId, String newUsername) {
        if (newUsername == null || newUsername.trim().isEmpty()) {
            return "用户名不能为空";
        }
        if (newUsername.length() < 2 || newUsername.length() > 20) {
            return "用户名长度应为2-20个字符";
        }

        User user = userMapper.findById(userId);
        if (user == null) {
            return "用户不存在";
        }

        // 检查新用户名是否已被其他用户使用
        User existUser = userMapper.findByUsername(newUsername);
        if (existUser != null && !existUser.getId().equals(userId.longValue())) {
            return "用户名已被占用";
        }

        int rows = userMapper.updateUsername(userId, newUsername);
        return rows > 0 ? "用户名修改成功" : "用户名修改失败";
    }
}