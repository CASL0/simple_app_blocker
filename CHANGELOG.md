# 変更履歴

- UPDATE
  - 下位互換がある変更
- ADD
  - 下位互換がある追加
- CHANGE
  - 下位互換のない変更
- FIX
  - バグ修正

## v1.2.7

- [UPDATE] androidx.appcompat:appcompat を v1.7.1 に更新

## v1.2.6

- [UPDATE] 16KiB メモリページサイズに対応
- [UPDATE] targetSDK バージョンアップ（33 → 34）
- [UPDATE] NDK バージョンアップ（27.0.12077973）
- [UPDATE] compileSDK バージョンアップ（33 → 34）、ライブラリバージョンアップ

## v1.2.5

- [UPDATE] Kotlin バージョンアップ（1.7.10 → 1.8.10）
- [UPDATE] ツールバーをデータバインディングに変更
- [ADD] ブロックログテーブルに app_name カラム追加
- [CHANGE] ブロックログテーブルの blocked_at カラムをエポック秒に修正

## v1.2.4

- [UPDATE] テーマアイコン対応
- [UPDATE] 接続許可アプリ追加画面で検索アイコンタップ後、検索ボックスにフォーカスを当てるように修正

## v1.2.3

- [UPDATE] Edge to Edge 対応

## v1.2.2

- [UPDATE] ブロックログが 0 個の時に、その旨を表示するように修正
- [UPDATE] Android12 未満でもスプラッシュ画面を表示するように修正

## v1.2.1

- [UPDATE] 「その他」画面 リンク項目のリップルの Round を 0dp に修正
- [UPDATE] アプリについての説明欄を追加
- [UPDATE] room のスキーマの export

## v1.2.0

- [UPDATE] マルチモジュール化
- [UPDATE] ライブラリのバージョン管理を、Gradle Version Catalog に移行
- [CHANGE] ブロックログの Card の色をプロトコルに応じて変化するように修正

## v1.1.5

- [UPDATE] フィルターの適用状態を SavedState に保存するように修正
- [UPDATE] NDK バージョンアップ（25.2.9519653）
- [UPDATE] PcapPlusPlus バージョンアップ（22.11）
- [UPDATE] 許可アプリ追加画面の検索ボックスを TopAppBar に移動
- [UPDATE] 許可ボタンは Favorite に修正し、許可の解除も行えるように修正

## v1.1.4

- [UPDATE]「その他」画面のレイアウトを修正
- [ADD] OSS ライセンス表示処理を追加
- [ADD] ソースコードのリンクを追加

## v1.1.3

- [UPDATE] VpnService のリクエストを ON_RESUME で実施するように修正
- [ADD] Room にブロックログのテーブルを追加

## v1.1.2

- [UPDATE] DI のライブラリを Dagger Hilt へ変更
- [UPDATE] 接続許可アプリ追加画面のインストール済みアプリ一覧をスワイプリフレッシュ可能に変更
-

## v1.1.1

- [ADD] Android13+で予測型「戻る」ジェスチャーをサポート
- [ADD] ローカライズ対応(英語)
- [ADD] Android13+でアプリ別の言語設定が可能なように android:localeConfig を追加
- [UPDATE] バージョン表記の文言修正

## v1.1.0

- [FIX] 接続許可アプリ追加画面にて検索でヒットしなかった場合もプログレス表示となっていたので修正
- [UPDATE] compileSDK バージョンアップ（31 → 33）、Kotlin バージョンアップ（1.6.10 → 1.7.10）、ライブラリバージョンアップ
- [UPDATE] targetSDK バージョンアップ（31 → 33）
- [ADD] Android13+で通知のランタイムパーミッションをリクエストする処理を追加
- [UPDATE] 許可アプリ一覧画面のカードの[x]ボタンのリップルを修正

## v1.0.2

- [UPDATE] 許可アプリ一覧画面にて、許可しているアプリが存在しないときはその旨を表示するように変更
- [UPDATE] 接続許可アプリ追加画面遷移時にインストール済みアプリをロード中はプログレス表示するように変更
- [UPDATE] 許可アプリ追加は Card のタップではなくアイコンボタンをクリックする方法に変更

## v1.0.1

- [UPDATE] 接続許可アプリ追加画面をアクティビティから BottomSheet へ変更
- [ADD] アプリ内アップデート機能を追加
