package me.zhenxin.zmusic.utils;

import me.zhenxin.zmusic.ZMusic;

import java.io.File;
import java.io.IOException;

/**
 * Cookie 工具类
 *
 * @author 真心
 * @email qgzhenxin@qq.com
 * @since 2023/3/21 12:25
 */
public class CookieUtils {
    private static final File COOKIE_FILE = new File(ZMusic.dataFolder, "cookies.txt");
    private static String cookieString = "";

    public static void initCookieManager() {
        if (!COOKIE_FILE.exists()) {
            try {
                COOKIE_FILE.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        cookieString = OtherUtils.readFileToString(COOKIE_FILE);
    }

    public static void saveCookies(String cookie) {
        cookieString = cookie;
        try {
            OtherUtils.saveStringToLocal(COOKIE_FILE, cookieString);
        } catch (IOException e) {
            ZMusic.log.sendDebugMessage("[CookieUtils] 保存Cookies失败: " + e.getMessage());
        }
    }

    public static String getCookies() {
        return cookieString;
    }
}
