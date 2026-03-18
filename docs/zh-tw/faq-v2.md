---
title: 常見問題 (V2)
---

# 常見問題 (V2)

注意：此處的常見問題僅適用於 V2 版本，V3 版本請查看 [常見問題](/zh-tw/faq)

## 播放時沒有聲音

如果您使用的是 `KCaldron`，那麼很抱歉，我們不支援此伺服器。

但如果您非要想使用模組服的話，這裡有兩種解決方案：

- 使用 [BungeeCord](https://www.spigotmc.org/wiki/bungeecord) 或者 [WaterFall](https://papermc.io/downloads#Waterfall) 等群組服伺服器端。
- 使用 Arclight、LoliServer、Mohist、Uranium 等 **可能** 支援本插件的模組服核心。

如果您使用的是 **Spigot、Paper、Yatopia、Sugarcane 等原版插件服核心**，那麼請檢查您是否滿足以下條件：

- 您的客戶端已經安裝了 ZMusic Mod（1.7.10、1.12 或以上版本）或者 AudioBuffer Mod（1.12 及以下版本）
- 您的伺服器端已正常安裝了 ZMusic 插件
- 您在伺服器插件資料夾安裝的是 ZMusic 系列插件而不是 ZMusic 模組（模組需要在客戶端進行安裝）
- 您的客戶端是 Fabric 或者 Forge 而**不是**純淨版，並且已經安裝了 AudioBuffer **或者** ZMusic 模組
- 您的網路環境良好
- 您點歌時使用的平台是網易雲音樂

如果你未滿足以上條件中的其中一個的話，請進行進一步調整。

當然，如果您全部滿足以上條件的話，還是無法播放音樂，我們建議您在未配置外置 API 的情況下使用 **網易雲音樂** 進行播放。

實際上，我們建議您使用自己擁有的伺服器開服。

## 伺服器載入成功後，使用 `/zm` 相關指令時，會提示「錯誤：請等待插件載入完畢」

如果您遇到本問題的話，請升級 ZMusic 插件到最新版本即可解決問題。

## 插件無法載入，提示「請卸載 AllMusic/AudioBuffer 插件」

請在伺服器插件資料夾刪除 AllMusic/AudioBuffer 插件即可。<br>
在新版本的 ZMusic 中，我們棄用了 BossBarAPI、ActionBarAPI 依賴插件與 AudioBuffer 前置插件，為了防止伺服器模組通訊頻道衝突，我們不得不採取本措施。

## 播放音樂成功後，客戶端提示耗時時間很長（但是實際卻很短）

導致此問題的原因可能是連續點擊同一個點歌按鈕間隔時間過短。導致內部線程調用尚未處理過來，顯示了錯誤的耗時時間，不影響實際使用。

## 登入網易雲音樂時，出現 `java.net.UnknownHostException` 異常

您的設定檔在配置外置 API 的時候域名配置錯誤，請檢查域名是否有效。<br>
如果域名有效的話，請檢查您伺服器的 DNS 是否正常或者刷新 DNS 快取。<br>
面板服用戶一般不會發生此種問題。

## 載入插件時，出現 `java.lang.NoClassDefFoundError com/google/gson/xxx` 錯誤

您的伺服器端未內置 gson 庫，安裝帶 gson 庫的插件即可。（不會吧不會吧，竟然真有不支援 gson 的吧！）

## 點歌成功後，後台刷屏報黃色錯誤

從 2.5 開始，舊版的進度調用方案已被停用，請更新到最新版本。

## 安裝 ZMusic 之後，客戶端無法啟動 (Fabric)

- 請檢查您安裝的模組版本是否對應，如您的客戶端使用的是 Fabric 模組載入器，但是您下載的是 `zmusic-forge-X.X.X-X.X.X.jar`<br>
- 如果您下載的是 Fabric 版本模組，請檢查您是否安裝了 `Fabric-API` 前置模組<br>
- 本模組與 `CardBoard` 不相容

## 插件載入後出現報錯：「錯誤：請等待插件載入完畢」

出現此種問題，請檢查以下情況。

- 檢查您安裝的插件版本是否為最新。
- 檢查 `ZMusic` 插件資料夾是否有權限寫入（特別是基於 Linux 的系統，~~建議直接暴力 chmod 777~~ 實際上，設置 777 是非常危險的行為）
- 檢查您的伺服器端是否支援異步線程（一般基於 `Bukkit` 的都支援，~~別問我關於 `Mohist` 的問題辣！焯！~~）
- 手動修復插件資料夾，在 `ZMusic` 資料夾中新建一個叫 `language` 的資料夾，之後從我們的倉庫中的 `master` 分支找到 `language` 資料夾，並下載 `zh_cn.json` 檔案，放入 `language` 資料夾中，然後重載或者重啟伺服器。

## 是否支援 `1.7.10`

我們並不推薦您使用 `1.7.10` 版本，如果您堅持使用，請確保如下說明。

- 僅支援 `Mohist/Uranium` 伺服器端
- `Uranium` 僅在 `dev-4-b210` 測試成功
- `Mohist` 僅在 `1.7.10-42` 測試成功
- 除 `聊天訊息` 外，其他全部歌詞顯示均無效（除 Uranium）
- 使用 `Uranium` 配套模組，可實現 Title/ActionBar 顯示
- 我們不接受任何 `1.7.10` 版本的問題反饋

## 插件卡服嗎？

<font size="25">不卡服！</font>
如果出現卡服問題，一般是伺服器內其他插件導致的卡服，並且某位用戶點歌的時候正好碰到了卡服的時刻。（不服？不服就貼出 timings 報告！）<br>
碎碎念：聽說點大悲咒會崩服（bushi<br>
![](/images/dabeizhou_1.png)<br>
![](/images/dabeizhou_2.png)<br>
如果您在點歌的時候確信發生了上述崩服的情況，請讓腐竹將崩服之前爆出的紅色 Stacktrace 發給我們，我們會盡快解決。<br>
為了您的人身安全，我們建議有群組服的將 ZMusic 丟進群組服插件資料夾內。

## 插件命令無法被 DeluxeMenu/TrMenu 等選單插件調用 (BungeeCord/WaterFall)

這是群組服的通病，因為 ZMusic 的指令是註冊在群組服核心內，而插件只能調用 Spigot/Paper 側的指令。<br>
這邊想出了一個折中的方法，就是讓用戶自己輸入，雖然這樣做對用戶來說不太友好，但是自己動手，豐衣足食！

## [zmusic 插件一直提示沒有 zmusic.use 權限，確保權限組已經加入了](https://www.mcbbs.net/forum.php?mod=viewthread&tid=1310665)

如果您使用的是蹦極端（BungeeCord/Waterfall），請在跨服端的插件目錄內添加 LuckPerms-Bungee 插件，然後通過 `/lpb` 指令進行賦予權限。

## [[Sponge]伺服器端 ZMusic 在線音樂載入不上去](https://www.mcbbs.net/forum.php?mod=viewthread&tid=1048579)

本插件不支援 Sponge 平台，請等待 3.0 發布。（當前的 3.0 測試版對 Sponge 為半支援狀態）

~~幹啥啥不行，自搜第一名！~~

## 我找不到以上描述的任何錯誤

您可以加入我們的交流群，如果有報錯訊息的話，請上傳後台報錯訊息至 [mclo.gs](https://mclo.gs)<br>
然後通過連結的形式反饋給我們。<br>
幫助手冊仍在進一步完善中！

## ~~彩蛋環節：死亡不掉落用什麼插件？~~

~~![](/images/keepInventory.png)~~<br>
~~你需要安裝下北澤式死亡不掉落插件，首先排除可以使用 `/gamerule` 改死亡不掉落，這怎麼可能是原版的指令呢，對吧！再說了，百度是什麼咱也完全不知道！~~
