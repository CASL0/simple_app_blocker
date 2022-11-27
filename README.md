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

- Kotlin 1.7.10
- Android Gradle Plugin 7.2.2
- Gradle 7.5.1
- JDK 11
- NDK 21.4.7075529

### ビルド

**Windows**

```cmd
git clone https://github.com/CASL0/simple_app_blocker.git
cd simple_app_blocker
bootstrap.bat
gradlew.bat build
```

**macOS**

```bash
git clone https://github.com/CASL0/simple_app_blocker.git
cd simple_app_blocker
sh bootstrap.sh
./gradlew build
```

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
