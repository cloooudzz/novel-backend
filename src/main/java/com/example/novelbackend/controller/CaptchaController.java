package com.example.novelbackend.controller;

import com.example.novelbackend.service.CaptchaService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    private final CaptchaService captchaService;

    public CaptchaController(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    /**
     * 获取验证码
     * @param request 用于获取客户端标识
     * @return 验证码图片Base64
     */
    @GetMapping("/get")
    public Map<String, Object> getCaptcha(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();

        // 使用sessionId或时间戳+随机数作为key
        String key = generateKey(request);
        String captchaImage = captchaService.generateCaptcha(key);

        result.put("code", 200);
        result.put("data", Map.of(
                "captchaKey", key,
                "captchaImage", captchaImage
        ));
        return result;
    }

    /**
     * 刷新验证码
     */
    @GetMapping("/refresh")
    public Map<String, Object> refreshCaptcha(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();

        // 清除旧的验证码
        String oldKey = request.getHeader("X-Captcha-Key");
        if (oldKey != null) {
            captchaService.removeCaptcha(oldKey);
        }

        String key = generateKey(request);
        String captchaImage = captchaService.generateCaptcha(key);

        result.put("code", 200);
        result.put("data", Map.of(
                "captchaKey", key,
                "captchaImage", captchaImage
        ));
        return result;
    }

    /**
     * 生成唯一标识
     */
    private String generateKey(HttpServletRequest request) {
        // 使用客户端IP + User-Agent + 时间戳的hash
        String ip = request.getRemoteAddr();
        String ua = request.getHeader("User-Agent");
        long timestamp = System.currentTimeMillis() / (60 * 1000); // 分钟级别，避免短时间内变化太快
        String raw = ip + ua + timestamp + System.nanoTime();
        return Integer.toHexString(raw.hashCode());
    }
}