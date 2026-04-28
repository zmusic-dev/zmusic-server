---
title: コマンド権限
---

# コマンド権限 {#command-permissions}

::: warning V4 ドキュメント
このバージョンはまだ開発中であり、エラーや不完全な部分がある可能性があります。

現在の長期サポートバージョンは V2 です。 [V2 ドキュメント](/ja/v2/)をご覧ください。
:::

::: info
V4 では基本コマンドと権限制御が利用できます。音楽再生、検索、停止、プレイリスト関連コマンドは入力できますが、具体的な機能はまだ開発中のため、現在は利用できない旨を表示します。
:::

## ルートコマンド {#root-command}

ZMusic のメインコマンドは `/zmusic` です。短いエイリアスも利用できます。

| コマンド | 必要な権限 | 用途 |
| --- | --- | --- |
| `/zmusic` | `zmusic.use` | ZMusic のルートコマンド。引数なしの場合はヘルプを表示 |
| `/music` | `zmusic.use` | `/zmusic` のエイリアス |
| `/zm` | `zmusic.use` | `/zmusic` のエイリアス |

## 現在利用可能なコマンド {#available-commands}

### ヘルプ {#help-command}

| コマンド | 必要な権限 | 用途 |
| --- | --- | --- |
| `/zmusic help` | `zmusic.help` | 利用可能なコマンド一覧を表示 |
| `/zmusic help <command>` | `zmusic.help` | 指定したコマンドのヘルプを表示 |

例：

```text
/zmusic help
/zmusic help reload
```

### プラグイン情報 {#info-command}

| コマンド | 必要な権限 | 用途 |
| --- | --- | --- |
| `/zmusic info` | `zmusic.info` | プラグインのバージョン、現在のプラットフォーム、オンライン人数を表示 |

例：

```text
/zmusic info
```

### 設定の再読み込み {#reload-command}

| コマンド | 必要な権限 | 用途 |
| --- | --- | --- |
| `/zmusic reload` | `zmusic.reload` | 設定ファイルと言語ファイルを再読み込み |

設定を変更した後に使用します。設定に問題がある場合は、再読み込み失敗として表示されます。

例：

```text
/zmusic reload
```

## 現在利用できないコマンド {#unavailable-commands}

以下のコマンドと権限は予約済みですが、音楽機能はまだ開発中です。現在実行すると、機能が利用できない旨を表示します。

| コマンド | 必要な権限 | 予定用途 |
| --- | --- | --- |
| `/zmusic play <keyword>` | `zmusic.play` | キーワードで音楽を再生 |
| `/zmusic search <keyword>` | `zmusic.search` | キーワードで音楽を検索 |
| `/zmusic stop` | `zmusic.stop` | 現在の再生を停止 |
| `/zmusic playlist list` | `zmusic.playlist` | 個人プレイリストを表示 |
| `/zmusic playlist create <name>` | `zmusic.playlist` | 個人プレイリストを作成 |
| `/zmusic playlist delete <name>` | `zmusic.playlist` | 個人プレイリストを削除 |
| `/zmusic playlist add <playlist> <song>` | `zmusic.playlist` | 個人プレイリストに曲を追加 |
| `/zmusic playlist remove <playlist> <song>` | `zmusic.playlist` | 個人プレイリストから曲を削除 |
| `/zmusic playlist play <playlist>` | `zmusic.playlist` | 個人プレイリストを再生 |
| `/zmusic playlist global <subcommand>` | `zmusic.admin` | グローバルプレイリストを管理 |

グローバルプレイリストのサブコマンドには `list`、`create`、`delete`、`add`、`remove`、`play` があります。すべて `zmusic.admin` 権限が必要です。
