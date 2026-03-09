package me.zhenxin.zmusic.login;

import java.lang.ref.PhantomReference;

public enum LoginMethod {
    QR("qr"),
    PHONE("phone"),
    EMAIL("email"),
    SEND_CODE("sendcode"),
    VERIFY("verify");

    private final String token;

    LoginMethod(String token) {
        this.token = token;
    }

    public static LoginMethod fromToken(String token) {
        if (token == null) {
            return null;
        }
        for (LoginMethod method : values()) {
            if (method.token.equalsIgnoreCase(token)) {
                return method;
            }
        }
        return null;
    }
}