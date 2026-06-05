package com.example.novelbackend.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

public class CaptchaUtil {

    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;
    private static final Random random = new Random();

    // 验证码字符集（排除容易混淆的字符）
    private static final String CHAR_SET = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";

    /**
     * 生成验证码图片（Base64格式）
     * @return 验证码信息
     */
    public static CaptchaInfo generateCaptcha() {
        String code = generateCode();
        String imageBase64 = generateImage(code);
        return new CaptchaInfo(code, "data:image/png;base64," + imageBase64);
    }

    /**
     * 生成随机验证码
     */
    private static String generateCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append(CHAR_SET.charAt(random.nextInt(CHAR_SET.length())));
        }
        return sb.toString();
    }

    /**
     * 生成验证码图片
     */
    private static String generateImage(String code) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 设置背景色
        g.setColor(new Color(245, 245, 245));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 绘制边框
        g.setColor(new Color(200, 200, 200));
        g.drawRect(0, 0, WIDTH - 1, HEIGHT - 1);

        // 绘制干扰线
        for (int i = 0; i < 50; i++) {
            g.setColor(getRandomColor(150, 200));
            int x1 = random.nextInt(WIDTH);
            int y1 = random.nextInt(HEIGHT);
            int x2 = random.nextInt(WIDTH);
            int y2 = random.nextInt(HEIGHT);
            g.drawLine(x1, y1, x2, y2);
        }

        // 绘制噪点
        for (int i = 0; i < 200; i++) {
            g.setColor(getRandomColor(100, 200));
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            g.fillRect(x, y, 1, 1);
        }

        // 绘制验证码文字
        for (int i = 0; i < code.length(); i++) {
            char c = code.charAt(i);
            g.setColor(getRandomColor(50, 150));

            // 随机字体大小
            int fontSize = 22 + random.nextInt(8);
            Font font = new Font("Arial", Font.BOLD, fontSize);
            g.setFont(font);

            // 随机旋转
            AffineTransform transform = new AffineTransform();
            transform.rotate(random.nextDouble() * 0.4 - 0.2,
                    WIDTH / 4 * i + 10, HEIGHT / 2);
            g.setTransform(transform);

            // 绘制字符
            int x = WIDTH / 4 * i + 8;
            int y = HEIGHT - 10;
            g.drawString(String.valueOf(c), x, y);

            // 重置变换
            g.setTransform(new AffineTransform());
        }

        g.dispose();

        // 转换为Base64
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取随机颜色
     */
    private static Color getRandomColor(int min, int max) {
        int r = min + random.nextInt(max - min);
        int g = min + random.nextInt(max - min);
        int b = min + random.nextInt(max - min);
        return new Color(r, g, b);
    }

    /**
     * 验证码信息类
     */
    public static class CaptchaInfo {
        private String code;
        private String image;

        public CaptchaInfo(String code, String image) {
            this.code = code;
            this.image = image;
        }

        public String getCode() { return code; }
        public String getImage() { return image; }
    }
}