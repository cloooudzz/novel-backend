package com.example.novelbackend.controller;

import com.example.novelbackend.entity.User;
import com.example.novelbackend.mapper.UserMapper;
import com.example.novelbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import java.io.File;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    // 注册接口（使用账号+用户名+密码）
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> params) {
        System.out.println("收到注册请求 - 账号: " + params.get("account") + ", 用户名: " + params.get("username"));
        Map<String, Object> result = new HashMap<>();

        String account = params.get("account");
        String username = params.get("username");
        String password = params.get("password");

        String msg = userService.register(account, username, password);
        if ("success".equals(msg)) {
            result.put("code", 200);
            result.put("msg", "注册成功");
        } else {
            result.put("code", 500);
            result.put("msg", msg);
        }
        return result;
    }

    // 登录接口（使用账号+密码）
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> params) {
        System.out.println("收到登录请求 - 账号: " + params.get("account"));
        Map<String, Object> result = new HashMap<>();

        String account = params.get("account");
        String password = params.get("password");

        String msg = userService.login(account, password);
        if ("success".equals(msg)) {
            User loggedUser = userMapper.findByAccount(account);
            result.put("code", 200);
            result.put("msg", "登录成功");
            result.put("userId", loggedUser.getId());
            result.put("username", loggedUser.getUsername());
            result.put("account", loggedUser.getAccount());
        } else {
            result.put("code", 500);
            result.put("msg", msg);
        }
        return result;
    }

    @Value("${avatar.upload.path}")
    private String uploadPath;

    @Value("${avatar.access.prefix}")
    private String accessPrefix;

    @PostMapping("/update-password")
    public Map<String, Object> changePassword(
            @RequestParam Integer userId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        System.out.println("收到修改密码请求 - userId: " + userId);
        Map<String, Object> result = new HashMap<>();
        String msg = userService.changePassword(userId, oldPassword, newPassword);
        result.put("code", msg.equals("密码修改成功") ? 200 : 400);
        result.put("msg", msg);
        return result;
    }

    @PostMapping("/change-avatar")
    public Map<String, Object> changeAvatar(
            @RequestParam Integer userId,
            @RequestParam MultipartFile avatar) {
        Map<String, Object> result = new HashMap<>();

        if (avatar == null || avatar.isEmpty()) {
            result.put("code", 400);
            result.put("msg", "请上传头像文件");
            return result;
        }

        String originalFilename = avatar.getOriginalFilename();
        String ext = FileUtil.extName(originalFilename);
        if (!ext.matches("jpg|png|gif|jpeg")) {
            result.put("code", 400);
            result.put("msg", "仅支持JPG/PNG/GIF格式");
            return result;
        }

        String fileName = userId + "_" + IdUtil.simpleUUID() + "." + ext;
        File destFile = new File(uploadPath + File.separator + fileName);

        try {
            FileUtil.mkdir(uploadPath);
            avatar.transferTo(destFile);

            String avatarPath = accessPrefix + fileName;
            String msg = userService.changeAvatar(userId, avatarPath);
            String fullAvatarUrl = "http://localhost:8080" + avatarPath;

            result.put("code", msg.equals("头像修改成功") ? 200 : 400);
            result.put("msg", msg);
            result.put("data", Map.of(
                    "avatarUrl", avatarPath,
                    "fullUrl", fullAvatarUrl
            ));
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "头像上传失败：" + e.getMessage());
        }
        return result;
    }

    @GetMapping("/current")
    public Map<String, Object> getCurrentUser(@RequestParam Integer userId) {
        Map<String, Object> result = new HashMap<>();
        User user = userMapper.findById(userId);
        if (user != null) {
            result.put("code", 200);
            Map<String, Object> data = new HashMap<>();
            data.put("id", user.getId());
            data.put("account", user.getAccount());
            data.put("username", user.getUsername());
            data.put("avatar", user.getAvatar());
            result.put("data", data);
        } else {
            result.put("code", 404);
            result.put("msg", "用户不存在");
        }
        return result;
    }

    @PostMapping("/update-username")
    public Map<String, Object> changeUsername(
            @RequestParam Integer userId,
            @RequestParam String newUsername) {
        System.out.println("收到修改用户名请求 - userId: " + userId + ", newUsername: " + newUsername);
        Map<String, Object> result = new HashMap<>();
        String msg = userService.changeUsername(userId, newUsername);

        if (msg.equals("用户名修改成功")) {
            result.put("code", 200);
            result.put("msg", msg);
            result.put("newUsername", newUsername);
        } else {
            result.put("code", 400);
            result.put("msg", msg);
        }
        return result;
    }
}