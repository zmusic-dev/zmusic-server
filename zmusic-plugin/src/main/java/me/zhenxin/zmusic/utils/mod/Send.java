package me.zhenxin.zmusic.utils.mod;


import com.google.gson.JsonObject;

public interface Send {

    void sendAM(Object playerObj, String data);

    default void sendAM(Object playerObj, int infoX, int infoY, int lyricX, int lyricY) {
        JsonObject data = new JsonObject();
        data.addProperty("EnableLyric", true);
        data.addProperty("EnableInfo", true);
        JsonObject info = new JsonObject();
        info.addProperty("x", infoX);
        info.addProperty("y", infoY);
        JsonObject lyric = new JsonObject();
        lyric.addProperty("x", lyricX);
        lyric.addProperty("y", lyricY);
        data.add("Info", info);
        data.add("Lyric", lyric);
        sendAM(playerObj, data.toString());
    }

    void sendABF(Object playerObj, String data);

    void sendToZMusicAddon(Object playerObj, String data);
}