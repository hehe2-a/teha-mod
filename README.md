# teha-hackcheck
> Supported versions: `1.21` / `1.21.1` / `1.21.4` / `1.21.8` / `1.21.11`

---

## Overview

**teha-hackcheck** is a simple mod that automatically scans your local mod folder during Minecraft startup to detect cheat clients (such as Meteor, Wurst, etc.) and suspicious keywords within JAR files.
It is designed to maintain fairness in multiplayer environments and help you verify that your own environment is secure and free from cheats.

---

### Preview

![Preview](https://cdn.modrinth.com/data/cached_images/24a2c9a64a37dea75156730f03c63dc969b47617_0.webp)

---

## Key Features

| Feature | Details |
| :--- | :--- |
| **Blacklist** | Instantly matches known cheat Mod IDs. |
| **Keyword Scan** | Scans strings within JAR files to identify potential cheats. |
| **Warning UI** | Displays a clear warning screen if a threat is detected. |
| **Privacy Focused** | All processing is performed locally. No data is sent to external servers. |

---

## Technical Information

### 🔨 How to Build

```bash
# JAR files will be generated in /build/libs
./gradlew build

# Build for a specific version
./gradlew :v1_21_8:build
```

### 🚀 How to Run (Development)

```bash
./gradlew :v1_21:runClient
./gradlew :v1_21_1:runClient
./gradlew :v1_21_4:runClient
./gradlew :v1_21_8:runClient
./gradlew :v1_21_11:runClient
```

### 📁 Project Structure

- `common/src/`: Common source code (changes reflect across all versions).
- `run/`: Shared world and settings across all versions.
- `v1_21_*/`: Version-specific configurations.
- `v1_21_*/mods/`: Place additional mods here for testing.

---

## Important Notes

> This mod uses pattern matching, which means there is a possibility of **false positives**.

---

Author: **はて、**

---

# (JP) teha-hackcheck
> 対応バージョン: `1.21` / `1.21.1` / `1.21.4` / `1.21.8` / `1.21.11`

---

## 概要

**teha-hackcheck** は、Minecraftの起動時にローカルのModフォルダーを自動スキャンし、チートクライアント（Meteor, Wurst等）や不審なキーワードを検知するシンプルなmodです。
マルチプレイヤーでの公平性維持や、自身の環境が安全（チートが入っていないか）を確認するために設計されています。

---

### プレビュー

![プレビュー](https://cdn.modrinth.com/data/cached_images/c7883f5561a43d662177140ac10dbe8d0a379fc5_0.webp)

---

## 主な機能

| 機能 | 内容 |
| :--- | :--- |
| **Blacklist** | 既知のチートMod IDを瞬時に照合します。 |
| **Keyword Scan** | JAR内の文字列をスキャンします。 |
| **Warning UI** | 検知された場合、警告画面を表示します。 |
| **Privacy Check** | 処理はすべてローカル。外部への通知は行いません。 |

---

## 技術的な詳細

### 🔨 ビルド方法

```bash
#/build/libs にjarファイルが生成されます
./gradlew build

#指定したバージョンのビルド
./gradlew :v1_21_8:build
```

### 🚀 クライアント起動（開発用）

```bash
./gradlew :v1_21:runClient
./gradlew :v1_21_1:runClient
./gradlew :v1_21_4:runClient
./gradlew :v1_21_8:runClient
./gradlew :v1_21_11:runClient
```

### 📁 構成

- `common/src/`: 共通ソースコード（ここを編集すれば全バージョンに反映）
- `run/`: 全バージョンで共通（ワールド・設定）
- `v1_21_*/`: 各バージョン用の設定
- `v1_21_*/mods/`: 各バージョンのMod追加場所

---

## 注意事項

> このModはパターンマッチングを利用しているため、**誤検知**の可能性が高いです

---

作者: **はて,**
