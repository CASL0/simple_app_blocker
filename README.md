# Simple App Blocker
[VpnService](https://developer.android.com/reference/android/net/VpnService)を利用し、指定のアプリ以外の通信をブロックするシンプルなユーティリティツールです。

## 機能
* ブロックした通信をリアルタイムで表示
* アプリ指定で通信許可

## 開発
### 開発環境
* Android Studio Bumblebee 2021.1.1 Patch 3
* Android Gradle Plugin 7.1.3
* Gradle 7.2
* JDK 11
* NDK 21.4.7075529

### 依存ライブラリ
* PcapPlusPlus 21.11
* com.orhanobut:logger 2.2.0
* org.greenrobot:eventbus 3.3.1

### ビルド
```bash
git clone https://github.com/CASL0/simple_app_blocker.git
cd simple_app_blocker
./gradlew build
```

## ドキュメント
* [docs](/docs)

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