package crm.commons.utils;


import cn.hutool.core.util.RandomUtil;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

public class PasswordEncoder {

    public static String encode(String password) {
        // 生成盐
        String salt = RandomUtil.randomString(20);
        // 调用加密函数
        return encode(password,salt);
    }
    private static String encode(String password, String salt) {
        // MD5加密
        return salt + "@" + DigestUtils.md5DigestAsHex((password + salt).getBytes(StandardCharsets.UTF_8));
    }
    public static Boolean matches(String encodedPassword, String rawPassword) {
        if (encodedPassword == null || rawPassword == null) {
            return false;
        }
        if(!encodedPassword.contains("@")){
            return false;
        }
        String[] arr = encodedPassword.split("@");
        // 获取盐
        String salt = arr[0];
        // 比较
        return encodedPassword.equals(encode(rawPassword, salt));
    }
}
