---
title: 常见问题 (V2)
---

# 常见问题 (V2)

注意：此处的常见问题仅适用于 V2 版本，V3 版本请查看 [常见问题](/faq)

## 播放时没有声音

如果您使用的是 `KCaldron`，那么很抱歉，我们不支持此服务器。

但如果您非要想使用模组服的话，这里有两种解决方案：

- 使用 [BungeeCord](https://www.spigotmc.org/wiki/bungeecord) 或者 [WaterFall](https://papermc.io/downloads#Waterfall) 等群组服服务端。
- 使用 Arclight、LoliServer、Mohist、Uranium 等 **可能** 支持本插件的模组服核心。

如果您使用的是 **Spigot、Paper、Yatopia、Sugarcane 等原版插件服核心**，那么请检查您是否满足以下条件：

- 您的客户端已经安装了 ZMusic Mod（1.7.10、1.12 或以上版本）或者 AudioBuffer Mod（1.12 及以下版本）
- 您的服务端已正常安装了 ZMusic 插件
- 您在服务器插件文件夹安装的是 ZMusic 系列插件而不是 ZMusic 模组（模组需要在客户端进行安装）
- 您的客户端是 Fabric 或者 Forge 而**不是**纯净版，并且已经安装了 AudioBuffer **或者** ZMusic 模组
- 您的网络环境良好
- 您点歌时使用的平台是网易云音乐

如果你未满足以上条件中的其中一个的话，请进行进一步调整。

当然，如果您全部满足以上条件的话，还是无法播放音乐，我们建议您在未配置外置 API 的情况下使用 **网易云音乐** 进行播放。

实际上，我们建议您使用自己拥有的服务器开服。

## 服务器载入成功后，使用 `/zm` 相关指令时，会提示“错误：请等待插件载入完毕”

如果您遇到本问题的话，请升级 ZMusic 插件到最新版本即可解决问题。

## 插件无法载入，提示“请卸载 AllMusic/AudioBuffer 插件”

请在服务器插件文件夹删除 AllMusic/AudioBuffer 插件即可。<br>
在新版本的 ZMusic 中，我们弃用了 BossBarAPI、ActionBarAPI 依赖插件与 AudioBuffer 前置插件，为了防止服务器模组通讯频道冲突，我们不得不采取本措施。

## 播放音乐成功后，客户端提示耗时时间很长（但是实际却很短）

导致此问题的原因可能是连续点击同一个点歌按钮间隔时间过短。导致内部线程调用尚未处理过来，显示了错误的耗时时间，不影响实际使用。

## 登录网易云音乐时，出现 `java.net.UnknownHostException` 异常

您的配置文件在配置外置 API 的时候域名配置错误，请检查域名是否有效。<br>
如果域名有效的话，请检查您服务器的 DNS 是否正常或者刷新 DNS 缓存。<br>
面板服用户一般不会发生此种问题。

## 载入插件时，出现 `java.lang.NoClassDefFoundError com/google/gson/xxx` 错误

您的服务端未内置 gson 库，安装带 gson 库的插件即可。（不会吧不会吧，竟然真有不支持 gson 的吧！）

## 点歌成功后，后台刷屏报黄色错误

从 2.5 开始，旧版的进度调用方案已被停用，请更新到最新版本。

## 安装 ZMusic 之后，客户端无法启动 (Fabric)

- 请检查您安装的模组版本是否对应，如您的客户端使用的是 Fabric 模组加载器，但是您下载的是 `zmusic-forge-X.X.X-X.X.X.jar`<br>
- 如果您下载的是 Fabric 版本模组，请检查您是否安装了 `Fabric-API` 前置模组<br>
- 本模组与 `CardBoard` 不兼容

## 插件加载后出现报错：“错误：请等待插件加载完毕”

出现此种问题，请检查以下情况。

- 检查您安装的插件版本是否为最新。
- 检查 `ZMusic` 插件文件夹是否有权限写入（特别是基于 Linux 的系统，~~建议直接暴力 chmod 777~~ 实际上，设置 777 是非常危险的行为）
- 检查您的服务端是否支持异步线程（一般基于 `Bukkit` 的都支持，~~别问我关于 `Mohist` 的问题辣！焯！~~）
- 手动修复插件文件夹，在 `ZMusic` 文件夹中新建一个叫 `language` 的文件夹，之后从我们的仓库中的 `master` 分支找到 `language` 文件夹，并下载 `zh_cn.json` 文件，放入 `language` 文件夹中，然后重载或者重启服务器。

## 是否支持 `1.7.10`

我们并不推荐您使用 `1.7.10` 版本，如果您坚持使用，请确保如下说明。

- 仅支持 `Mohist/Uranium` 服务端
- `Uranium` 仅在 `dev-4-b210` 测试成功
- `Mohist` 仅在 `1.7.10-42` 测试成功
- 除 `聊天信息` 外，其他全部歌词显示均无效（除 Uranium）
- 使用 `Uranium` 配套模组，可实现 Title/ActionBar 显示
- 我们不接受任何 `1.7.10` 版本的问题反馈

## 插件卡服吗？

<font size="25">不卡服！</font>
如果出现卡服问题，一般是服务器内其他插件导致的卡服，并且某位用户点歌的时候正好碰到了卡服的时刻。（不服？不服就贴出 timings 报告！）<br>
碎碎念：听说点大悲咒会崩服（bushi<br>
![](/images/dabeizhou_1.png)<br>
![](/images/dabeizhou_2.png)<br>
如果您在点歌的时候确信发生了上述崩服的情况，请让腐竹将崩服之前爆出的红色 Stacktrace 发给我们，我们会尽快解决。<br>
为了您的人身安全，我们建议有群组服的将 ZMusic 丢进群组服插件文件夹内。

## 插件命令无法被 DeluxeMenu/TrMenu 等菜单插件调用 (BungeeCord/WaterFall)

这是群组服的通病，因为 ZMusic 的指令是注册在群组服核心内，而插件只能调用 Spigot/Paper 侧的指令。<br>
这边想出了一个折中的方法，就是让用户自己输入，虽然这样做对用户来说不太友好，但是自己动手，丰衣足食！

## [zmusic 插件一直提示没有 zmusic.use 权限，确保权限组已经加入了](https://www.mcbbs.net/forum.php?mod=viewthread&tid=1310665)

如果您使用的是蹦极端（BungeeCord/Waterfall），请在跨服端的插件目录内添加 LuckPerms-Bungee 插件，然后通过 `/lpb` 指令进行赋予权限。

## [[Sponge]服务端 ZMusic 在线音乐加载不上去](https://www.mcbbs.net/forum.php?mod=viewthread&tid=1048579)

本插件不支持 Sponge 平台，请等待 3.0 发布。（当前的 3.0 测试版对 Sponge 为半支持状态）

~~干啥啥不行，自搜第一名！~~

## 我找不到以上描述的任何错误

您可以加入我们的交流群，如果有报错信息的话，请上传后台报错信息至 [mclo.gs](https://mclo.gs)<br>
然后通过链接的形式反馈给我们。<br>
帮助手册仍在进一步完善中！

## ~~彩蛋环节：死亡不掉落用什么插件？~~

~~![](/images/keepInventory.png)~~<br>
~~你需要安装下北泽式死亡不掉落插件，首先排除可以使用 `/gamerule` 改死亡不掉落，这怎么可能是原版的指令呢，对吧！再说了，百度是什么咱也完全不知道！~~
