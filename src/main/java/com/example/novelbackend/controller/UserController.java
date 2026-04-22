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


// 跨域配置，解决前后端端口不同的问题
@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        System.out.println("收到注册请求：" + user.getUsername()); // 加日志，看后端有没有收到请求
        Map<String, Object> result = new HashMap<>();
        String msg = userService.register(user.getUsername(), user.getPassword());
        if ("success".equals(msg)) {
            result.put("code", 200);
            result.put("msg", "注册成功");
        } else {
            result.put("code", 500);
            result.put("msg", msg);
        }
        return result;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {
        System.out.println("收到登录请求：" + user.getUsername());
        Map<String, Object> result = new HashMap<>();
        String msg = userService.login(user.getUsername(), user.getPassword());
        if ("success".equals(msg)) {
            // 获取完整用户信息
            User loggedUser = userMapper.findByUsername(user.getUsername());
            result.put("code", 200);
            result.put("msg", "登录成功");
            result.put("userId", loggedUser.getId());  // 返回 userId
            result.put("username", loggedUser.getUsername());
        } else {
            result.put("code", 500);
            result.put("msg", msg);
        }
        return result;
    }

    // 头像上传路径（在application.properties中配置）
    @Value("${avatar.upload.path}")
    private String uploadPath;

    // 头像访问前缀（前端访问头像的URL前缀）
    @Value("${avatar.access.prefix}")
    private String accessPrefix;

    // 接口1：修改密码
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

    // 接口2：上传并修改头像
    @PostMapping("/change-avatar")
    public Map<String, Object> changeAvatar(
            @RequestParam Integer userId,
            @RequestParam MultipartFile avatar) {
        Map<String, Object> result = new HashMap<>();

        // 1. 校验文件
        if (avatar == null || avatar.isEmpty()) {
            result.put("code", 400);
            result.put("msg", "请上传头像文件");
            return result;
        }

        // 2. 校验文件类型
        String originalFilename = avatar.getOriginalFilename();
        String ext = FileUtil.extName(originalFilename);
        if (!ext.matches("jpg|png|gif|jpeg")) {
            result.put("code", 400);
            result.put("msg", "仅支持JPG/PNG/GIF格式");
            return result;
        }

        // 3. 生成唯一文件名
        String fileName = userId + "_" + IdUtil.simpleUUID() + "." + ext;
        File destFile = new File(uploadPath + File.separator + fileName);

        try {
            // 4. 保存文件
            FileUtil.mkdir(uploadPath);
            avatar.transferTo(destFile);

            // 5. 生成访问路径
            String avatarPath = accessPrefix + fileName;

            // 6. 更新数据库
            String msg = userService.changeAvatar(userId, avatarPath);

            // 7. 构建完整的URL（加上服务器地址）
            String fullAvatarUrl = "http://localhost:8080" + avatarPath;

            result.put("code", msg.equals("头像修改成功") ? 200 : 400);
            result.put("msg", msg);
            result.put("data", Map.of(
                    "avatarUrl", avatarPath,      // 相对路径
                    "fullUrl", fullAvatarUrl      // 完整URL
            ));
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "头像上传失败：" + e.getMessage());
        }
        return result;
    }

    // 获取当前用户信息（包括头像）
    @GetMapping("/current")
    public Map<String, Object> getCurrentUser(@RequestParam Integer userId) {
        Map<String, Object> result = new HashMap<>();
        User user = userMapper.findById(userId);
        if (user != null) {
            result.put("code", 200);
            Map<String, Object> data = new HashMap<>();
            data.put("id", user.getId());
            data.put("username", user.getUsername());
            data.put("avatar", user.getAvatar());
            result.put("data", data);
        } else {
            result.put("code", 404);
            result.put("msg", "用户不存在");
        }
        return result;
    }

    // 接口3：修改用户名
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
            result.put("newUsername", newUsername);  // 返回新用户名
        } else {
            result.put("code", 400);
            result.put("msg", msg);
        }
        return result;
    }

}