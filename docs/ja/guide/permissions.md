---
title: 権限
---

# 権限

::: warning V4 ドキュメント
このバージョンはまだ開発中であり、エラーや不完全な部分がある可能性があります。

現在の長期サポートバージョンは V2 です。 [V2 ドキュメント](/ja/v2/)をご覧ください。
:::

::: info
V4R の権限ノードは機能ごとに分割されています。一部のコマンドはまだ開発中であり、これらのノードは今後のコマンド機能接続で使用されます。
:::

## 一般プレイヤー権限 {#player-permissions}

| 権限ノード | 説明 | デフォルト |
| --- | --- | --- |
| `zmusic.use` | ZMusic のルートコマンドを使用 | true |
| `zmusic.help` | ヘルプを表示 | true |
| `zmusic.info` | プラグイン状態を表示 | true |
| `zmusic.play` | 音楽を再生 | true |
| `zmusic.search` | 音楽を検索 | true |
| `zmusic.stop` | 再生を停止 | true |
| `zmusic.volume` | 音量を調整 | true |
| `zmusic.lyric` | 歌詞を表示 | true |
| `zmusic.playlist` | プレイリストを管理 | true |

## 管理者権限 {#admin-permissions}

| 権限ノード | 説明 | デフォルト |
| --- | --- | --- |
| `zmusic.reload` | 設定を再読み込み | op |
| `zmusic.admin` | すべての管理者コマンドを使用 | op |

## プラットフォーム補足 {#platform-notes}

- Bukkit は `plugin.yml` でデフォルト権限を宣言します。
- BungeeCord と Velocity はプラットフォーム標準の権限システムを通じて `hasPermission` を確認し、プラグインメタデータではデフォルト権限を宣言しません。
