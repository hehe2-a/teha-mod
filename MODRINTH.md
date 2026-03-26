# teha-hackcheck

> これはFabric 1.21.8+向けのクライアントサイドmodです。

---

## 概要 / Overview

**teha-hackcheck** は、Minecraftの起動時にローカルのModフォルダーを自動スキャンし、チートクライアント（Meteor, Wurst等）や不審なキーワードを検知するシンプルなmodです。
マルチプレイヤーでの公平性維持や、自身の環境が安全（チートが入っていないか）を確認するために設計されています。

---

### イメージ

![イメージ](https://cdn.modrinth.com/data/cached_images/65bddd4e9714d7c71f72c24d94353cf78799ec7b_0.webp)

---

## 主な機能

| 機能 | 内容 |
| :--- | :--- |
| **Blacklist** | 既知のチートMod IDを瞬時に照合します。 
| **Keyword Scan** | JAR内の文字列をスキャンします。
| **Warning UI** | 検知された場合、警告画面を表示します。
| **Privacy Check** | 処理はすべてローカル。外部への通知は行いません。 

---

## 注意事項

> このModはパターンマッチングを利用しているため、**誤検知**の可能性が高いです

---

作者: **はて、**
