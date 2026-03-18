---
title: V2 使用文件
---

# v2 使用文件

歡迎查看 ZMusic 說明文件，這裡有所有您需要的幫助，如果您需要排查無法播放聲音等問題，請點 [此處](/zh-tw/faq-v2)。

## 簡介

這是一個功能強大的音樂系統，支援以下功能。

- 全服點歌
- 單獨播放
- 歌詞顯示
- 歌詞翻譯顯示
- 多搜尋源（網易雲/嗶哩嗶哩）
- 關鍵字搜尋
- 個人歌單
- 全服歌單
- 歌單播放（網易雲）
- 音量調節（1.12 及以上支援）
- 支援 BungeeCord

## 客戶端 Mod 說明

本插件需要客戶端安裝配套 Mod 才能正常播放

- 對於 1.8-1.11 的客戶端，請安裝 [AudioBuffer](https://www.mcbbs.net/thread-832205-1-1.html) Mod
- 對於 1.12.2-1.19 的客戶端，請安裝 [ZMusic](https://github.com/zmusic-dev/zmusic-client/releases) Mod

## BungeeCord 說明

- 只有 BungeeCord 端需要安裝 ZMusic-Plugin 插件（子服不需要安裝）
- BungeeCord 目前僅支援 1.9-1.19 的伺服器端
- BungeeCord 暫不支援經濟系統
- 對於子服顯示 Papi 變量、進度提示等功能，只需要對子服安裝 ZMusic-Addon 插件

## 影片

[點擊前往嗶哩嗶哩查看演示影片](https://www.bilibili.com/video/av92156922)

## 反饋

- 前往 [碼雲](https://gitee.com/zmusic-dev/zmusic-server) 或 [GitHub](https://github.com/zmusic-dev/zmusic-server) 提交 Issues
- 加入交流群：[1032722724](https://jq.qq.com/?_wv=1027&k=5oIs7cc) 反饋

# 命令

## 主命令

- `/zm` 主命令
- `/zm help` 查看幫助
- `/zm play` 播放音樂
- `/zm music` 全服點歌
- `/zm search` 搜尋音樂
- `/zm playlist` 歌單系統
- `/zm stop` 停止播放
- `/zm login` 登入網易雲音樂

## 播放

通過歌名搜尋一個音樂，直接播放

### 命令

`/zm play [搜尋源] [歌名]`

[搜尋源說明](#搜尋源說明)

### 範例

`/zm play netease 你的貓咪`

## 點歌

通過歌名搜尋一個音樂，全服發送後，玩家點擊播放

### 命令

`/zm music [搜尋源] [歌名]`

[搜尋源說明](#搜尋源說明) [歌名 ID 化說明](#歌名-id-化說明)

### 範例

`/zm music netease 你的貓咪`

## 搜尋

通過歌名搜尋一個音樂，返回十首音樂的列表

### 命令

`/zm search [搜尋源] [歌名]`

[搜尋源說明](#搜尋源說明) [歌名 ID 化說明](#歌名-id-化說明)

### 範例

`/zm search netease 你的貓咪`

## 歌單

通過導入歌單存儲在伺服器，方便播放歌單。

### 命令

`/zm playlist [平台] [子命令]`

目前支援以下平台

- netease/163 - 網易雲音樂

如果平台為 type 則為設置歌單播放方式。目前支援

- normal - 順序播放
- loop - 循環播放
- random - 隨機播放

範例：
`/zm playlist type random`

如果平台為 global 則為全局歌單模式。

- 子命令與普通模式相同

範例：
`/zm playlist global netease list`

`子命令` 對應平台的子命令

- `import` 通過歌單連結導入歌單
- 參數：`歌單連結` 對應平台的歌單連結
- `list` 檢索指定平台的歌單列表
- `play` 通過歌單 ID 播放歌單（可用 list 獲取）
- 參數：`歌單ID` 指定平台的歌單 ID

### 範例

導入：

- `/zm playlist 163 import https://music.163.com/#/playlist?id=363046232`

播放：

- `/zm playlist 163 play 363046232`

## 管理員

管理員相關操作，全服強制播放，重載設定等

### 命令

`/zm playall [搜尋源] [歌名]` 強制全服播放
`/zm stopAll` 強制停止全服播放
`/zm reload` 重載設定檔

[搜尋源說明](#搜尋源說明) [歌名 ID 化說明](#歌名-id-化說明)

### 範例

`/zm playAll netease 你的貓咪`

# 權限

## 普通玩家權限

`zmusic.use` 可使用 play、stop 等普通指令

## 管理員權限

`zmusic.admin` 可使用 playAll、stopAll 等管理員指令

# 設定檔

```json
{
  // 設定檔版本(請勿修改)
  "version": 11,
  // 是否字段檢查更新
  "check-update": true,
  // 插件提示訊息顯示前綴
  "prefix": "&bZMusic &e>>> &r",
  // 是否開啟除錯模式
  "debug": false,
  // API設定
  "api": {
    // 網易雲音樂API地址
    //
    // 使用開源專案NeteaseCloudMusicApi
    // 推薦自行部署，需Node.js環境
    // 地址: https://github.com/Binaryify/NeteaseCloudMusicApi
    "netease": "https://ncm.zhenxin.me/"
  },
  // 是否關注作者的網易雲音樂帳號
  "netease-follow": true,
  // ZMusic VIP 設定
  "vip": {
    // 授權帳號
    "account": "1307993674",
    "secret": "none"
  },
  // 點歌設定
  "music": {
    // 點歌扣除的金幣(設置為0則不扣除)
    // 擁有zmusic.bypass的玩家無視扣除
    "money": 10,
    // 點歌的冷卻時間(設置為0則無冷卻)
    // 擁有zmusic.bypass的玩家無視冷卻
    "cooldown": 5
  },
  // 歌詞設定
  "lyric": {
    // 是否啟用歌詞
    "enable": true,
    // 是否顯示歌詞翻譯
    "showLyricTr": true,
    // 歌詞顏色
    "color": "&b",
    // 以下為顯示方式設置，可同時啟用
    // 是否使用BossBar顯示歌詞(不支援1.8及以下)
    "bossBar": true,
    // 是否使用ActionBar顯示歌詞
    "actionBar": false,
    // 是否使用Title顯示歌詞
    "subTitle": false,
    // 是否使用聊天訊息顯示歌詞
    "chatMessage": false,
    // Hud 設定(僅支援1.12及以上)
    "hud": {
      // 是否啟用Hud
      "enable": true,
      // 訊息的X坐標
      "infoX": 2,
      // 訊息的Y坐標
      "infoY": 12,
      // 歌詞的X坐標
      "lyricX": 2,
      // 歌詞的Y坐標
      "lyricY": 72
    }
  }
}
```

# 變量

- `%zmusic_playing_name%` 獲取當前播放的音樂歌名
- `%zmusic_playing_singer%` 獲取當前播放的音樂歌手
- `%zmusic_playing_lyric%` 獲取當前時間顯示的歌詞
- `%zmusic_time_current%` 獲取當前播放的音樂的時間
- `%zmusic_time_max%` 獲取當前播放的音樂的最大時間
- `%zmusic_playing_platform%` 獲取當前播放的音樂平台
- `%zmusic_playing_source%` 獲取當前播放的音樂來源

# 前置插件

## 全版本使用

- [`PlaceholderAPI`](https://www.spigotmc.org/resources/placeholderapi.6245/) [可選] 如需使用上方變量，請安裝
- [`Vault`](https://www.spigotmc.org/resources/vault.34315/) [可選] 如果需要使用點歌扣費，請安裝

## 1.5,1.6 版本使用

- ~~[`AudioBuffer`](https://www.mcbbs.net/thread-832205-1-1.html) [必須] 用於播放音樂，貼內有配套 Mod 客戶端需安裝~~

## 1.4 及以下版本使用

- ~~[`AudioBuffer`](https://www.mcbbs.net/thread-832205-1-1.html) [必須] 用於播放音樂，貼內有配套 Mod 客戶端需安裝~~
- ~~[`BossBarAPI`](https://www.mcbbs.net/thread-729531-1-1.html) [可選] 如需使用 BossBar 顯示歌詞，請安裝~~
- ~~[`ActionBarAPI`](https://www.spigotmc.org/resources/actionbarapi-1-8-1-14-2.1315/) [可選] 如需使用 ActionBar 顯示歌詞，請安裝~~

# 搜尋源說明

`搜尋源` 為你要搜尋音樂的平台，目前支援以下平台：

- netease/163 - 網易雲音樂
- kuwo - 酷我音樂
- bilibili - 嗶哩嗶哩音樂

**QQ 音樂 API 已經完全移除，酷狗音樂播放時會出現問題**

# 歌名 ID 化說明

- 將歌名替換為 `-id:音樂ID` 即可
- 目前支援網易雲、嗶哩嗶哩音樂
- 範例：`/zm play bilibili -id:374305`
