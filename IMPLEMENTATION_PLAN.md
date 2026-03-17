# 命令与权限模块设计

## 1. 背景与现状

当前仓库已经具备以下基础能力：

- 统一的发送者抽象 `ZCommandSender`
- 统一的玩家抽象 `ZPlayer`
- 平台聚合服务 `PlatformService`
- 三端平台实体适配：
  - Bukkit
  - BungeeCord
  - Velocity

但目前还缺少：

- 命令注册与分发框架
- 子命令定义模型
- 参数解析与错误提示
- 权限节点集中定义
- 权限校验与统一拒绝提示
- 平台侧命令注册适配

这意味着业务功能即使开始开发，也没有统一入口，后续容易出现三端实现分叉、权限命名不一致、帮助文本难维护的问题。

## 2. 设计目标

本模块需要满足以下目标：

1. 统一三端命令开发方式，业务层只写一次子命令逻辑。
2. 命令与权限集中定义，避免散落的硬编码字符串。
3. 支持后续扩展：
   - `/zmusic play`
   - `/zmusic stop`
   - `/zmusic info`
   - `/zmusic help`
   - `/zmusic reload`
   - `/zmusic playlist ...`
4. 支持玩家/控制台差异校验。
5. 支持权限缺失、参数错误、执行异常的统一反馈。
6. 尽量不引入新依赖，优先使用现有抽象与 Kotlin 基础能力。

## 3. 模块边界

建议在 `zmusic-core` 中新增命令核心，平台模块只负责“注册”和“适配”。

### 3.1 `zmusic-core` 负责

- 命令模型
- 命令调度器
- 参数切片与路由
- 权限定义
- 统一异常与提示
- 帮助信息生成
- 命令上下文对象

### 3.2 `zmusic-bukkit / zmusic-bungee / zmusic-velocity` 负责

- 将平台原生命令事件转换为 `ZCommandSender + args`
- 注册根命令 `zmusic`
- 将平台发送者包装成 `BukkitCommandSender / BungeeCommandSender / VelocityCommandSender`
- 调用 core 命令调度器

## 4. 推荐目录结构

建议新增如下结构：

```text
zmusic-core/src/main/kotlin/me/zhenxin/zmusic/command/
  ZMusicCommandManager.kt
  CommandContext.kt
  CommandResult.kt
  CommandException.kt
  CommandNode.kt
  RootCommand.kt
  args/
    ArgumentReader.kt
  permission/
    Permission.kt
    Permissions.kt
  builtin/
    HelpCommand.kt
    InfoCommand.kt
    ReloadCommand.kt
    PlayCommand.kt
    StopCommand.kt
```

平台侧建议新增：

```text
zmusic-bukkit/.../platform/command/BukkitCommandRegistrar.kt
zmusic-bungee/.../platform/command/BungeeCommandRegistrar.kt
zmusic-velocity/.../platform/command/VelocityCommandRegistrar.kt
```

## 5. 核心对象设计

## 5.1 `CommandContext`

作用：承载一次命令执行所需的上下文。

建议字段：

- `sender: ZCommandSender`
- `label: String`
- `args: List<String>`
- `raw: String`
- `platformService: PlatformService`

建议能力：

- `requirePlayer(): ZPlayer`
- `reply(message: String)`
- `reply(component: Component)`
- `hasPermission(permission: Permission): Boolean`
- `checkPermission(permission: Permission)`

说明：

- `requirePlayer()` 在控制台执行玩家专属命令时抛出统一异常。
- `checkPermission()` 统一抛出权限不足异常，避免每个子命令重复写提示。

## 5.2 `Permission`

作用：集中描述一个权限节点，而不是满项目散落字符串常量。

建议字段：

- `node: String`
- `description: String`
- `defaultGrantedToConsole: Boolean = true`

说明：

- 是否默认允许控制台不是平台权限系统语义，而是命令层的业务语义。
- 例如“仅玩家可执行”的命令，即使控制台拥有 `*`，仍应被 `requirePlayer()` 拦截。

## 5.3 `Permissions`

集中维护权限常量，避免硬编码。

建议节点：

- `zmusic.use`
- `zmusic.help`
- `zmusic.info`
- `zmusic.play`
- `zmusic.stop`
- `zmusic.reload`
- `zmusic.playlist`
- `zmusic.admin`

建议映射关系：

- 普通用户命令：
  - `use`
  - `help`
  - `info`
  - `play`
  - `stop`
- 管理命令：
  - `reload`
  - `admin`
- 组合策略：
  - `admin` 可视为管理命令兜底权限

## 5.4 `CommandNode`

作用：抽象一个可执行命令节点。

建议字段：

- `name: String`
- `aliases: Set<String>`
- `permission: Permission?`
- `playerOnly: Boolean`
- `description: String`
- `usage: String`
- `children: List<CommandNode>`

建议方法：

- `matches(input: String): Boolean`
- `execute(context: CommandContext)`
- `suggest(context: CommandContext): List<String>` 先预留，不急着实现补全

说明：

- 根命令和子命令都实现同一抽象，后续扩展简单。
- `usage` 用于帮助页和参数错误提示。

## 5.5 `ZMusicCommandManager`

作用：命令入口调度器。

职责：

- 注册根命令与所有子命令
- 分发到对应子命令
- 在执行前完成：
  - 子命令查找
  - 权限校验
  - 玩家限制校验
  - 异常转换为用户可读消息
- 无参数时默认展示帮助

核心流程：

1. 收到 `sender + label + args`
2. 如果无参数，执行 `help`
3. 根据第一个参数匹配子命令
4. 找不到则提示未知子命令并回显帮助
5. 命中后校验权限
6. 校验是否仅玩家可执行
7. 调用子命令执行逻辑
8. 捕获 `CommandException` 并统一回复
9. 捕获未知异常并记录日志

## 6. 命令树设计

第一阶段只做一个稳定的 MVP 命令树。

### 6.1 根命令

```text
/zmusic
/zmusic help [command]
/zmusic info
/zmusic play <keyword>
/zmusic stop
/zmusic reload
```

### 6.2 命令职责

#### `/zmusic`

- 无参数默认进入帮助页
- 权限：`zmusic.use`

#### `/zmusic help [command]`

- 显示命令帮助
- 如果指定子命令，仅显示该子命令用法
- 权限：`zmusic.help`

#### `/zmusic info`

- 展示插件版本、平台、当前状态、搜索源状态
- 权限：`zmusic.info`

#### `/zmusic play <keyword>`

- 播放关键词检索结果
- MVP 先只支持单关键词串联，不做复杂参数语法
- 权限：`zmusic.play`
- 默认仅玩家可执行

#### `/zmusic stop`

- 停止当前玩家或当前上下文播放
- 权限：`zmusic.stop`
- 默认仅玩家可执行

#### `/zmusic reload`

- 重载配置和语言文件
- 权限：`zmusic.reload` 或 `zmusic.admin`

## 7. 权限策略设计

## 7.1 命名规则

统一前缀：

```text
zmusic.<action>
```

禁止：

- `music.*`
- `zmusics.*`
- 平台特有前缀

这样能保证文档、实现、权限插件配置一致。

## 7.2 判定顺序

建议执行顺序：

1. 根命令存在性
2. 权限校验
3. 发送者类型校验
4. 参数合法性校验
5. 业务执行

原因：

- 权限不足时不泄露过多业务细节
- 控制台执行玩家命令时提示更明确
- 参数错误只在有权限的前提下提示

## 7.3 权限继承策略

建议命令层采用“显式或关系”：

- 通过当前节点权限
- 或拥有 `zmusic.admin`

不要在命令层自行解析通配符 `*`，交给平台权限系统处理。

## 7.4 玩家与控制台策略

需要区分两个概念：

- `有权限`
- `允许该类型发送者执行`

例如：

- `/zmusic reload`
  - 控制台允许
  - 玩家需要管理权限
- `/zmusic play`
  - MVP 先仅玩家允许
  - 控制台即使有权限也提示“该命令只能由玩家执行”

## 8. 错误模型

建议新增统一命令异常：

- `CommandException`
- `NoPermissionCommandException`
- `PlayerOnlyCommandException`
- `UsageCommandException`
- `CommandNotFoundException`

统一反馈好处：

- 命令实现更干净
- 国际化更容易
- 不同平台行为一致

## 9. 国际化补充

建议为命令模块补齐如下 i18n 键：

```text
command.no_permission
command.player_only
command.unknown_subcommand
command.invalid_usage
command.help.header
command.help.entry
command.reload.success
command.reload.failed
command.info.header
```

说明：

- 现有 `help.tips` 可以保留，但不足以支撑完整命令交互。
- 命令层不要硬编码中文/英文提示。

## 10. 平台接入设计

## 10.1 Bukkit

实现方式：

- 在 `plugin.yml` 注册 `zmusic`
- `JavaPlugin` 中绑定 `CommandExecutor`，可选 `TabCompleter`
- 收到命令后包装 `BukkitCommandSender`
- 调用 `ZMusicCommandManager.execute(...)`

## 10.2 BungeeCord

实现方式：

- 新增 `Command` 子类
- 注册到插件管理器
- 包装 `BungeeCommandSender`
- 调用 core 命令调度器

## 10.3 Velocity

实现方式：

- 使用 Velocity 命令管理器注册根命令
- 包装 `VelocityCommandSender`
- 调用 core 命令调度器

## 11. 与后续业务模块的关系

命令模块不直接承载音乐业务，只负责入口编排。

建议后续业务层再补：

- `MusicApplicationService`
- `PlaybackService`
- `SearchService`
- `PlaylistService`

命令只调用应用服务，不直接写搜索、播放、持久化逻辑。

## 12. 分阶段实施

### 阶段 1：命令骨架

目标：

- 建立 `CommandContext / Permission / CommandNode / ZMusicCommandManager`
- 实现 `help / info / reload` 三个命令
- 接通三端根命令

验收：

- 三端都能执行 `/zmusic`
- 权限不足时有统一提示
- 控制台/玩家区分正确

### 阶段 2：首个业务命令

目标：

- 接入 `/zmusic play <keyword>`
- 接入 `/zmusic stop`
- 命令层与未来业务服务边界稳定

验收：

- 可从命令进入业务链路
- 参数校验与错误提示完整

### 阶段 3：帮助与补全增强

目标：

- 帮助页按权限过滤
- 增加基础 tab 补全
- 改善 usage 输出

### 阶段 4：权限与文档收口

目标：

- 补全文档中的权限节点说明
- 在各平台描述文件中声明权限
- 加测试覆盖命令调度和权限判定

## 13. 当前最推荐的实现顺序

建议下一步按下面顺序开发：

1. 先做 `Permissions + CommandContext + CommandException`
2. 再做 `CommandNode + ZMusicCommandManager`
3. 先接 `help / info / reload`
4. 再接三端命令注册
5. 最后接 `play / stop`

这样可以尽快形成一个稳定骨架，再把真正的音乐业务挂进去。

## 14. 暂不建议现在做的事情

当前阶段不建议立刻做：

- 复杂 DSL 命令框架
- 注解式自动注册
- 复杂参数类型系统
- 通配符权限树自实现
- 多级 playlist 子命令全集

原因：

- 当前项目仍处在基础模块重建阶段
- 先做简单、可控、可测试的命令骨架更稳
- 等业务能力稳定后再扩展命令体验成本更低

