# AGENTS.md

此文件为 AI 智能体提供项目开发指导。

## 项目概览

ZMusic 是一个 Minecraft 音乐系统插件，支持多平台（Bukkit/Spigot、BungeeCord、Velocity）部署。采用 Kotlin + Java 混合开发，使用 Gradle 多模块架构。

## 核心架构

### 模块结构和职责

```
zmusic-runtime  → 运行时依赖加载器（Java，独立）
    ↑
zmusic-core     → 核心业务逻辑（Kotlin，依赖 runtime）
    ↑
平台适配层：
├── zmusic-bukkit   → Bukkit/Spigot/Paper/Folia 适配
├── zmusic-bungee   → BungeeCord 代理适配
└── zmusic-velocity → Velocity 代理适配（需要 Java 21）
```

### 关键设计模式

1. **平台抽象**：通过 `PlatformLogger` 接口和 `Platform` 枚举实现跨平台
2. **运行时依赖注入**：`zmusic-runtime` 动态下载和加载依赖，避免版本冲突
3. **Shadow JAR + 重定位**：将依赖打包并重定位到 `me.zhenxin.zmusic.libs` 包下

## 开发命令

### 构建命令

```bash
# 完整构建（生成 Shadow JAR 到 build/libs）
./gradlew clean build

# 构建特定模块
./gradlew :zmusic-bukkit:shadowJar    # 生成 Bukkit 插件
./gradlew :zmusic-bungee:shadowJar    # 生成 BungeeCord 插件
./gradlew :zmusic-velocity:shadowJar  # 生成 Velocity 插件（需要 JDK 21）

# 刷新依赖
./gradlew --refresh-dependencies
```

### 调试和开发

```bash
# 查看构建详情
./gradlew build --info

# 处理资源文件（替换版本变量）
./gradlew processResources

# 检查依赖树
./gradlew dependencies
```

## 代码组织规范

### 包结构
- `me.zhenxin.zmusic` - 主包
- `me.zhenxin.zmusic.config` - 配置和国际化
- `me.zhenxin.zmusic.platform` - 平台适配接口
- `me.zhenxin.zmusic.utils` - 工具类和扩展函数
- `me.zhenxin.zmusic.dependencies` - 运行时依赖管理

### 文件命名
- Kotlin 类：PascalCase（如 `ZMusic.kt`）
- Java 类：PascalCase（如 `ZMusicRuntime.java`）
- 配置文件：小写（如 `config.toml`）

## 配置系统

使用 TOML 格式配置（Night Config 库）：
- 配置文件：`{dataFolder}/config.toml`
- 国际化文件：`resources/i18n/{en-US,zh-CN}.toml`
- 配置版本：通过 `config-version` 字段管理

## 依赖管理

### 版本集中管理
所有依赖版本定义在 `gradle/libs.versions.toml`

### 关键依赖
- Kotlin：2.3.10
- Hutool：5.8.44（工具库）
- Night Config：3.8.3（TOML 解析）
- Adventure：4.26.1（文本组件）

### Maven 仓库
- PaperMC：`https://repo.papermc.io/repository/maven-public/`
- TabooProject：`https://repo.tabooproject.org/repository/releases/`

## 平台兼容性

| 平台 | Java 版本 | API 版本 |
|------|----------|----------|
| Bukkit/Spigot | Java 8+ | 1.21.11-R0.2-SNAPSHOT |
| BungeeCord | Java 8+ | 1.21-R0.4 |
| Velocity | Java 21+ | 3.4.0-SNAPSHOT |

## 版本注入

使用 Blossom 插件在构建时注入版本信息：
- 模板文件：`src/main/java-templates` 或 `src/main/kotlin-templates`
- 版本变量：`@version@` → Git commit hash 或 "4.0.0-dev"

## 重要文件路径

- 插件配置：`plugin.yml`（Bukkit）、`bungee.yml`（BungeeCord）、`velocity-plugin.json`（Velocity）
- 构建输出：`build/libs/zmusic-{platform}.jar`
- 依赖缓存：运行时下载到服务器的 `libraries` 目录

## 注意事项

1. **Java 版本**：大部分模块使用 Java 8，但 Velocity 模块需要 Java 21
2. **依赖重定位**：所有依赖都会重定位到 `me.zhenxin.zmusic.libs` 包下
3. **运行时依赖**：某些依赖（如 Netty、Guava）会在运行时动态下载
4. **测试**：项目目前没有单元测试，添加新功能时建议补充测试

## 文档站点

项目文档位于 `docs/` 目录，基于 VitePress 构建。

### 文档命令

```bash
# 安装依赖
pnpm install

# 启动开发服务器
pnpm dev

# 构建生产版本
pnpm build

# 预览构建结果
pnpm preview
```

### 文档结构

```text
docs/
├── .vitepress/
│   ├── config.mts         # VitePress 主配置
│   └── theme/             # 自定义主题
│       ├── components/    # 自定义组件
│       ├── data/          # 数据文件
│       ├── utils/         # 工具函数（i18n 等）
│       ├── index.ts       # 主题入口
│       └── style.css      # 全局样式
├── guide/                 # 中文指南
├── en/                    # 英文文档
├── ja/                    # 日文文档
└── public/                # 静态资源
```

### 多语言配置

路由映射：
- `/`：简体中文 (root locale)
- `/en/`：English
- `/ja/`：日本語

新增页面需同步更新 `docs/.vitepress/config.mts` 中的 `localeSidebarCopy` 和调用处。

### 国际化系统

文档使用集中式 i18n 系统，位于 `docs/.vitepress/theme/utils/i18n.ts`：

```typescript
import { useI18n, useLocale, getSiteLocale } from '../utils/i18n'

// 获取翻译文本
const t = useI18n()
// 模板中使用：{{ t.loading }}
// 脚本中使用：t.value.loading

// 获取当前语言
const locale = useLocale() // 返回 '/' | '/en/' | '/ja/'
```

**支持的翻译键**：`note`, `loading`, `error`, `download`, `viewRelease`, `viewBuild`, `devWarning`, `loader`, `mcVersion`, `select`, `noResult`, `tabs.{stable,dev,addon}`

### 组件架构

#### 下载组件

| 组件 | 用途 |
|------|------|
| `DownloadTable.vue` | 服务端插件下载（稳定版/开发版/Addon） |
| `ModDownload.vue` | 客户端 Mod 下载（Fabric/Forge 选择器） |
| `ExternalLinks.vue` | 服务端外部链接（Codeberg/Gitee/SpigotMC） |
| `ModExternalLinks.vue` | 客户端外部链接（Modrinth/CurseForge） |
| `ExternalLinkCard.vue` | 通用外部链接卡片（被上述两个组件复用） |

#### 其他组件

| 组件 | 用途 |
|------|------|
| `GiscusComments.vue` | GitHub Discussions 评论（使用 @giscus/vue） |
| `SiteIcon.vue` | 功能图标（使用 @lucide/vue） |
| `HomeHeroScene.vue` | 首页 Hero 动画场景 |
| `NeteaseApiTable.vue` | 网易云 API 列表展示 |

#### 图标库

- **品牌图标**：使用 `simple-icons` npm 包（Codeberg、Gitee、SpigotMC、Modrinth、CurseForge）
- **功能图标**：使用 `@lucide/vue` npm 包

### 部署

推送到 `main` 分支后，GitHub Actions 执行构建并部署到服务器。
