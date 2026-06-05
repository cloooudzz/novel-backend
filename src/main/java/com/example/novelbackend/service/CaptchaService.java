package com.example.novelbackend.service;

import com.example.novelbackend.utils.CaptchaUtil;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CaptchaService {

    // 存储验证码，key为sessionId/时间戳，value为验证码
    // 使用ConcurrentHashMap存储，实际生产环境建议使用Redis
    private final Map<String, CaptchaInfo> captchaStore = new ConcurrentHashMap<>();

    // 验证码有效期（毫秒）
    private static final long CAPTCHA_EXPIRE_TIME = 5 * 60 * 1000; // 5分钟

    /**
     * 生成验证码
     * @param key 唯一标识
     * @return Base64格式的验证码图片
     */
    public String generateCaptcha(String key) {
        CaptchaUtil.CaptchaInfo captchaInfo = CaptchaUtil.generateCaptcha();
        captchaStore.put(key, new CaptchaInfo(captchaInfo.getCode(), System.currentTimeMillis()));
        return captchaInfo.getImage();
    }

    /**
     * 验证验证码
     * @param key 唯一标识
     * @param code 用户输入的验证码
     * @return 是否有效
     */
    public boolean validateCaptcha(String key, String code) {
        CaptchaInfo info = captchaStore.get(key);
        if (info == null) {
            return false;
        }

        // 检查是否过期
        if (System.currentTimeMillis() - info.timestamp > CAPTCHA_EXPIRE_TIME) {
            captchaStore.remove(key);
            return false;
        }

        // 验证码不区分大小写
        boolean valid = info.code.equalsIgnoreCase(code);
        if (valid) {
            // 验证成功后删除，防止重用
            captchaStore.remove(key);
        }
        return valid;
    }

    /**
     * 清除验证码
     */
    public void removeCaptcha(String key) {
        captchaStore.remove(key);
    }

    private static class CaptchaInfo {
        String code;
        long timestamp;

        CaptchaInfo(String code, long timestamp) {
            this.code = code;
            this.timestamp = timestamp;
        }
    }
}