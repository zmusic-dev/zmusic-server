---
title: コマンド
---

# コマンド

::: warning V4 ドキュメント
このバージョンはまだ開発中であり、エラーや不完全な部分がある可能性があります。

現在の長期サポートバージョンは V2 です。 [V2 ドキュメント](/ja/v2/)をご覧ください。
:::

::: info
V4R のコマンドシステムでは、コマンド登録と権限チェックが接続済みです。音楽再生、検索、停止、プレイリスト関連コマンドは入口が予約されていますが、音楽バックエンドはまだ開発中のため、現在は機能が利用できない旨を表示します。
:::

## ルートコマンド {#root-command}

| コマンド | 説明 |
| --- | --- |
| `/zmusic` | ZMusic のルートコマンド |
| `/music` | `/zmusic` のエイリアス |
| `/zm` | `/zmusic` のエイリアス |

## 利用可能なコマンド {#available-commands}

| コマンド | 権限 | 説明 |
| --- | --- | --- |
| `/zmusic help [command]` | `zmusic.help` | コマンドヘルプを表示 |
| `/zmusic info` | `zmusic.info` | プラグインのバージョン、プラットフォーム、オンライン人数を表示 |
| `/zmusic reload` | `zmusic.reload` | 設定と言語ファイルを再読み込み |

## 登録済みだが現在利用できないコマンド {#reserved-commands}

| コマンド | 権限 | 予定用途 |
| --- | --- | --- |
| `/zmusic play <keyword>` | `zmusic.play` | 音楽を再生 |
| `/zmusic search <keyword>` | `zmusic.search` | 音楽を検索 |
| `/zmusic stop` | `zmusic.stop` | 再生を停止 |
| `/zmusic playlist <list\|create\|delete\|add\|remove\|play\|global>` | `zmusic.playlist` | 個人またはグローバルプレイリストを管理 |

`playlist global` とそのサブコマンドには `zmusic.admin` 権限が必要です。
