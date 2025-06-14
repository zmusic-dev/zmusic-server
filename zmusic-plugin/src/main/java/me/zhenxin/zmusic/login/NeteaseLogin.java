package me.zhenxin.zmusic.login;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.zhenxin.zmusic.ZMusic;
import me.zhenxin.zmusic.config.Config;
import me.zhenxin.zmusic.utils.CookieUtils;
import me.zhenxin.zmusic.utils.NetUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 网易云登录
 *
 * @author 真心
 * @email qgzhenxin@qq.com
 * @since 2023/3/21 11:17
 */
public class NeteaseLogin {
    private static final String API = Config.neteaseApiRoot;
    private static final Gson GSON = new Gson();

    public static String create(String key) throws UnsupportedEncodingException {
        String params = "key=" + key;
        String result = NetUtils.postNetString(API + "login/qr/create", null, params);
        JsonObject json = GSON.fromJson(result, JsonObject.class);
        JsonObject data = json.getAsJsonObject("data");
        String url = data.get("qrurl").getAsString();
        return "https://api.2dcode.biz/v1/create-qr-code?data=" + URLEncoder.encode(url, "UTF-8");
    }

    public static Integer check(String key) {
        String params = "key=" + key + "&noCookie=true";
        String result = NetUtils.postNetString(API + "login/qr/check", null, params);
        JsonObject json = GSON.fromJson(result, JsonObject.class);
        if (json.get("code").getAsInt() == 803) {
            String cookie = json.get("cookie").getAsString();
            CookieUtils.saveCookies(cookie);
        }
        return json.get("code").getAsInt();
    }

    public static String key() {
        String result = NetUtils.postNetString(API + "login/qr/key", null, "");
        JsonObject json = GSON.fromJson(result, JsonObject.class);
        JsonObject data = json.getAsJsonObject("data");
        return data.get("unikey").getAsString();
    }

    public static void welcome() {
        String nickname = nickname();
        if (!nickname.isEmpty()) {
            ZMusic.log.sendNormalMessage("您已登录网易云音乐, 昵称: " + nickname);
        } else {
            ZMusic.log.sendErrorMessage("您未登录网易云音乐, 自动转为匿名登录.");
            ZMusic.log.sendErrorMessage("匿名登录将无法获取您的个人信息, 仅能播放公开音乐.");
            ZMusic.log.sendErrorMessage("请使用 /zm login 命令登录网易云音乐.");
        }
    }

    public static String nickname() {
        JsonObject data = status();
        try {
            int code = data.get("code").getAsInt();
            if (code != 200) {
                return "";
            }
            JsonObject profile = data.getAsJsonObject("profile");
            if (profile == null) {
                return "";
            }
            String nickname = profile.get("nickname").getAsString();
            if (nickname == null || nickname.isEmpty()) {
                return "";
            }
            return nickname;
        } catch (Exception e) {
            return "";
        }
    }

    private static JsonObject status() {
        String result = NetUtils.postNetString(API + "login/status", null, "");
        JsonObject root = GSON.fromJson(result, JsonObject.class);
        return root.getAsJsonObject("data");
    }
}
