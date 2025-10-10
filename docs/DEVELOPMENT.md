# 開発用資料

## ビルドする

1. 以下のコマンドを実行し、JNI から使用しているネイティブライブラリ（Pcap++）をセットアップする

    ```bash
    ./gradlew setup
    ```

1. Android Studio の最新版をインストールする
1. ▷を押下し、アプリを実行する

## Formatter

[Spotless plugin for Gradle](https://github.com/diffplug/spotless/tree/main/plugin-gradle) 経由で各言語向けの formatter を実行している

- Kotlin: [ktfmt](https://github.com/facebook/ktfmt)
  - バージョン: 0.56
- C++: [clang-format](https://github.com/llvm/llvm-project)
  - バージョン: 18.1.3
- Gradle: [groovy-eclipse](https://github.com/groovy/groovy-eclipse)
- Markdown: [prettier](https://prettier.io/)

Jetbrains の[Spotlessプラグイン](https://plugins.jetbrains.com/plugin/18321-spotless-gradle)を使ってフォーマッタを実行する

## PlayConsole へのアップロード

fastlane を使用しアップロードする

### 準備

- PlayConsole へアップロード用の Service Account を作成し、リポジトリルートに JSON を配置する
- [`fastlane/metadata/android`](/fastlane/metadata/android) に PlayStore 掲載用のリリースノートを新規作成する
  - [`fastlane/metadata/android/ja-JP/changelogs`](/fastlane/metadata/android/ja-JP/changelogs)
  - [`fastlane/metadata/android/en-US/changelogs`](/fastlane/metadata/android/en-US/changelogs)

### アップロード

次のコマンドで aab をアップロードする

```bash
bundle config --local path vendor/bundle
bundle install
bundle exec fastlane android deploy
```
