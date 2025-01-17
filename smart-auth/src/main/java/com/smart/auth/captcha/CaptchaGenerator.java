package com.smart.auth.captcha;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
public class CaptchaGenerator {

    private static final String CAPTCHA_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    /**
     * 验证码长度
     */
    private static final int CAPTCHA_LENGTH = 4;
    private static final int IMG_WIDTH = 120;
    private static final int IMG_HEIGHT = 40;

    /**
     * 生成验证码
     *
     * @return java.util.Map<java.lang.String, java.lang.String>
     */
    @SneakyThrows
    public static Map<String, String> createCaptcha() {
        Map<String, String> map = new HashMap<>(0);
        String captchaStr = generateCaptchaString();
        map.put("captcha", captchaStr);
        BufferedImage img = createImage(captchaStr);
        String image = "data:image/png;base64," + toBase64(img);
        map.put("image", image);
        return map;
    }

    private static String generateCaptchaString() {
        StringBuilder sb = new StringBuilder(CAPTCHA_LENGTH);
        Random r = new Random();
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            sb.append(CAPTCHA_CHARS.charAt(r.nextInt(CAPTCHA_CHARS.length())));
        }
        return sb.toString();
    }

    private static BufferedImage createImage(String captchaStr) throws Exception {
        BufferedImage img = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();

        // Fill the background color
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, IMG_WIDTH, IMG_HEIGHT);

        // Set font and color for the text
        Font font = new Font("Arial", Font.BOLD, 30);
        g2d.setFont(font);
        g2d.setColor(Color.BLACK);

        // Draw the captcha string on the image
        for (int i = 0; i < captchaStr.length(); i++) {
            char c = captchaStr.charAt(i);
            g2d.drawChars(captchaStr.toCharArray(), i, 1, 10 + i * 20, 30);
        }

        // Add some noise lines
        g2d.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < 5; i++) {
            int x1 = (int) (Math.random() * IMG_WIDTH);
            int y1 = (int) (Math.random() * IMG_HEIGHT);
            int x2 = (int) (Math.random() * IMG_WIDTH);
            int y2 = (int) (Math.random() * IMG_HEIGHT);
            g2d.drawLine(x1, y1, x2, y2);
        }

        g2d.dispose();
        return img;
    }

    private static String toBase64(BufferedImage img) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(img, "png", os);
        byte[] bytes = os.toByteArray();
        return Base64.getEncoder().encodeToString(bytes);
    }
}