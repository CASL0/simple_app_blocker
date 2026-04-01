# 開発ガイド

## 前提条件

| ツール | バージョン |
| --- | --- |
| Android Studio | 最新安定版 |
| JDK | 17 |
| NDK | 27.0.12077973 |
| CMake | 3.18.1 以上 |

NDK と CMake は Android Studio の SDK Manager からインストールできる。

## セットアップ

### 1. ネイティブライブラリの取得

`core:pcapplusplus` モジュールは JNI 経由で [PcapPlusPlus](https://github.com/seladb/PcapPlusPlus) を使用する。ビルド前に以下のコマンドでプリビルドライブラリをダウンロードする。

```bash
./gradlew setup
```

このタスクは GitHub Releases から PcapPlusPlus v22.11 の Android 向けプリビルドをダウンロードし、`core/pcapplusplus/libs/` に展開する。対応 ABI は以下の通り。

- `armeabi-v7a`
- `arm64-v8a`
- `x86`
- `x86_64`

### 2. ビルド

```bash
# デバッグビルド
./gradlew assembleDebug

# リリースバンドル
./gradlew bundleRelease
```

リリースビルドにはリポジトリルートに `keystore.properties` が必要となる。

```properties
keyAlias=...
keyPassword=...
storeFile=...
storePassword=...
```

## テスト

### ユニットテスト (JVM)

```bash
# 全モジュール
./gradlew test

# 特定のテストクラスのみ
./gradlew :core:data:test --tests "*.DefaultAllowlistRepositoryTest"
```

テストフレームワーク: JUnit 4、Mockito、Kotlin Coroutines Test

### インストルメンテッドテスト

```bash
# 実機またはエミュレーター接続時
./gradlew connectedAndroidTest
```

テストフレームワーク: AndroidX Test、Room Testing

#### マネージドバーチャルデバイス

`core:data` と `core:database` モジュールでは、Gradle Managed Devices を構成している。

| デバイス名 | デバイス | API Level | イメージ |
| --- | --- | --- | --- |
| `pixel6api29` | Pixel 6 | 29 | google |
| `pixel6api33` | Pixel 6 | 33 | google |

### コードカバレッジ

[Kover](https://github.com/Kotlin/kotlinx-kover) でカバレッジを計測する。対象モジュールは `core:data`、`feature:rule_change`、`feature:blocklog`。

```bash
./gradlew koverXmlReportDebug
```

## コードスタイル

[Spotless](https://github.com/diffplug/spotless/tree/main/plugin-gradle) でフォーマットを統一する。

| 言語 | フォーマッター | スタイル |
| --- | --- | --- |
| Kotlin | [ktfmt](https://github.com/facebook/ktfmt) 0.56 | Google スタイル、最大行幅 80 文字 |
| C++ | [clang-format](https://clang.llvm.org/docs/ClangFormat.html) 18.1.3 | Chromium スタイル |
| Gradle | [groovy-eclipse](https://github.com/groovy/groovy-eclipse) | デフォルト |
| Markdown | [prettier](https://prettier.io/) | デフォルト |

```bash
# フォーマットチェック
./gradlew spotlessCheck

# フォーマット適用
./gradlew spotlessApply
```

コミット前に `spotlessApply` を実行すること。CI (`check.yml`) で毎 push 時に `spotlessCheck` が実行される。

### Lint

```bash
./gradlew lint
```

## CI

GitHub Actions で以下のワークフローを実行する。

### check.yml

- **トリガー**: 毎 push
- **内容**: clang-format 18 のインストール後、`spotlessCheck` を実行

### build.yml

- **トリガー**: 毎 push、毎週月曜日 (UTC 0:00)
- **内容**: Java 17 / 21 / 25 のマトリックスで `assembleDebug` を並列実行

### unit_test.yml

- **トリガー**: `main` への push（`**.kt` ファイルの変更時）
- **内容**: `koverXmlReportDebug` を実行し、カバレッジレポートを [Codecov](https://app.codecov.io/github/CASL0/simple_app_blocker) にアップロード

## 依存関係の管理

[Renovate](https://docs.renovatebot.com/) で依存関係を自動更新する。

- minor/patch アップデートは自動マージ
- major アップデートは手動レビュー

設定は [`renovate.json`](/renovate.json) を参照。

## Play Console へのアップロード

[Fastlane](https://fastlane.tools/) を使用してアップロードする。

### 準備

1. Play Console へアップロード用の Service Account を作成し、JSON をリポジトリルートに配置する
2. [`fastlane/metadata/android`](/fastlane/metadata/android) にリリースノートを作成する
   - [`ja-JP/changelogs`](/fastlane/metadata/android/ja-JP/changelogs)
   - [`en-US/changelogs`](/fastlane/metadata/android/en-US/changelogs)

### アップロード

```bash
bundle config --local path vendor/bundle
bundle install
bundle exec fastlane android deploy
```

`deploy` レーンはリリース AAB をビルドし、Google Play の beta トラックにアップロードする。

## 依存ライブラリ

全依存関係は [`gradle/libs.versions.toml`](/gradle/libs.versions.toml) で管理している。
