# 4.0.0-dev

> 注意：此版本仍在开发中，可能存在错误或不完善的地方。

## 重构

- 使用 Kotlin 重写整个项目
- 模块化架构设计
  - `zmusic-runtime`: 运行时依赖加载器
  - `zmusic-core`: 核心业务逻辑
  - `zmusic-bukkit`: Bukkit/Spigot/Paper/Folia 适配
  - `zmusic-bungee`: BungeeCord 代理适配
  - `zmusic-velocity`: Velocity 代理适配 (需要 Java 21)
- 运行时依赖注入，避免版本冲突
- Shadow JAR + 重定位，将依赖打包到 `me.zhenxin.zmusic.libs` 包下

## 新功能

- 支持 Folia 平台
- 支持 Velocity 3.4.0+
- 使用 TOML 格式配置文件 (Night Config 库)
- 多语言支持 (简体中文、English、日本語)
- 完整的 API 支持

## 技术栈

- Kotlin 2.3.10
- Java 8+ (Velocity 模块需要 Java 21)
- Gradle 多模块构建
- Hutool 工具库
- Adventure 文本组件
