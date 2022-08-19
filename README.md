# Simple App Blocker

[VpnService](https://developer.android.com/reference/android/net/VpnService)を利用し、指定のアプリ以外の通信をブロックするシンプルなユーティリティツールです。

<a href='https://play.google.com/store/apps/details?id=jp.co.casl0.android.simpleappblocker'>
    <img alt='Get it on Google Play' height="80" src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png'/></a>

## 機能

- ブロックした通信をリアルタイムで表示
- アプリ指定で通信許可
- IPv4/IPv6 対応

## 開発

### 開発環境

- Android Gradle Plugin 7.1.3
- Gradle 7.2
- JDK 11
- NDK 21.4.7075529

### 依存ライブラリ

- Accompanist 0.16.0
- PcapPlusPlus 21.11
- com.orhanobut:logger 2.2.0
- org.greenrobot:eventbus 3.3.1

### ビルド

- 本リポジトリをクローンした後、[bootstrap.bat](/bootstrap.bat)を実行してください。
- cmd を起動し、`gradlew.bat build`を実行してください。

## ドキュメント

- [docs](/docs)

## ライセンス

```
Copyright 2022 CASL0

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
