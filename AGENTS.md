# 仓库贡献指南

## 项目结构与模块组织

这是 ZMusic Minecraft 插件的 Gradle 多模块 Java 项目。主插件代码位于
`zmusic-plugin/src/main/java/me/zhenxin/zmusic`，运行资源位于
`zmusic-plugin/src/main/resources`，包括 `plugin.yml`、`bungee.yml`、
`velocity-plugin.json`、`config.json` 和内置语言文件。`zmusic-addon` 是配套
Addon 模块。跨版本 NMS 实现位于 `zmusic-nms`：公共接口在
`zmusic-nms/zmusic-nms-core`，旧版本实现集中在
`zmusic-nms/zmusic-nms-legacy`，其他子模块按 Minecraft 版本拆分。根目录
`language/` 存放外部语言元数据和翻译文件。

## 构建、测试与开发命令

使用仓库内置 Gradle Wrapper。

- `./gradlew build`：构建全部模块，并生成 shaded 插件 Jar。
- `./gradlew :zmusic-plugin:shadowJar`：只构建主插件产物。
- `./gradlew :zmusic-addon:shadowJar`：只构建 Addon 产物。
- `./gradlew clean build`：清理旧构建输出后完整重建。

构建产物位于各模块的 `build/libs/`。项目使用 Java 21 toolchain，并在适用模块
中目标输出 Java 8 字节码。

## 编码风格与命名约定

Java 源码使用 UTF-8 和 4 空格缩进。包名保持在 `me.zhenxin.zmusic` 下。新增
平台相关实现时遵循现有后缀，例如 `*Bukkit`、`*BC`、`*Velocity`。NMS 类名应
包含目标 Minecraft 修订号，例如 `AdvancementPacket_1_20_R1`。Java 中优先使用
`import`，不要写全限定类名。除非变更确实需要，不新增依赖。

## 测试指南

当前仓库没有专门的 `src/test` 目录。提交变更前至少运行 `./gradlew build`。涉及
平台行为时，需要在对应服务端手动验证：Bukkit/Paper、BungeeCord 或 Velocity。
修改跨版本 NMS 代码时，要针对受影响的 Minecraft 版本分别检查。

## 提交与 Pull Request 规范

近期提交使用简洁的 Conventional Commits，常见格式包括
`feat: 添加 Velocity 代理服务器支持`、`fix: 修复低版本无法启动...`、
`chore: 升级版本至 2.11.1`。继续使用 `feat:`、`fix:`、`build:`、`chore:` 等
前缀，并用一句话说明变更。

PR 应说明行为变化、列出已测试的平台或版本、关联相关 issue，并标明是否修改了
配置或语言文件。若变更影响游戏内可见消息，请附日志或截图。

## 安全与配置注意事项

不要提交账号 Cookie、音乐服务 Token、服务器凭据或本地测试服文件。修改
`zmusic-plugin/src/main/resources/config.json` 的默认配置时，尽量保持向后兼容。
