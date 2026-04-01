# セキュリティポリシー

本ドキュメントは、Simple App Blocker のセキュリティ方針を [OWASP Mobile Application Security Verification Standard (MASVS) v2](https://mas.owasp.org/MASVS/) に基づいて定めるものである。

MASVS は OWASP (Open Worldwide Application Security Project) が策定するモバイルアプリケーション向けのセキュリティ検証標準であり、以下の 8 カテゴリでセキュリティ要件を体系化している。

> The OWASP MASVS (Mobile Application Security Verification Standard) is the industry standard for mobile app security. It can be used by mobile software architects and developers seeking to develop secure mobile applications, as well as security testers to ensure completeness and consistency of test results.
>
> --- [OWASP MASVS](https://mas.owasp.org/MASVS/)

本プロジェクトでは MAS Testing Profiles の **L1** (基本的なセキュリティベースライン) への準拠を目標とし、VPN アプリとしての特性を踏まえた追加方針を定める。

---

## MASVS-STORAGE: データストレージ

> MASVS-STORAGE focuses on the secure handling of sensitive data, ensuring it is properly stored, protected from unauthorized access, and safely managed throughout its lifecycle.
>
> --- [MASVS-STORAGE](https://mas.owasp.org/MASVS/05-MASVS-STORAGE/)

本アプリはパケットフィルタリングのログ（送信元/送信先 IP アドレス、ポート番号、プロトコル、パッケージ名）を Room データベースに保存する。これらはユーザーのネットワーク行動に関わる情報であり、適切な保護が必要である。

### 方針

#### MASVS-STORAGE-1: 機密データの安全な保存

- アプリが保存するデータの機密レベルを定義し、それに応じた保護手段を適用する
- Android の内部ストレージ (`/data/data/<package>`) にデータベースを配置し、他アプリからのアクセスを防止する
- `android:allowBackup="false"` を設定し、デバイスバックアップによるデータ流出を防止する
- ブロックログに対してデータ保持期間の上限を設け、不要になったデータを自動的に削除する

#### MASVS-STORAGE-2: ログ・クリップボードへの機密データ漏洩防止

- リリースビルドではログ出力ライブラリの呼び出しを R8 (ProGuard) により除去し、ログへの機密情報出力を防止する
- IP アドレスやポート番号などのネットワーク情報をシステムログに出力しない
- パケット情報をクリップボードにコピーする機能を提供する場合は、Android 13 以降のクリップボード自動消去に対応する

---

## MASVS-CRYPTO: 暗号化

> MASVS-CRYPTO covers cryptographic practices, ensuring that the app uses up-to-date, strong cryptography and follows key management best practices.
>
> --- [MASVS-CRYPTO](https://mas.owasp.org/MASVS/06-MASVS-CRYPTO/)

本アプリはユーザー認証情報や暗号鍵を扱わないため、暗号化の適用範囲は保存データの保護に限定される。

### 方針

#### MASVS-CRYPTO-1: 業界標準の暗号アルゴリズムの使用

- 暗号機能を導入する場合は、Android プラットフォームが提供する標準 API（Android Keystore System、javax.crypto）を使用する
- 独自の暗号アルゴリズムの実装は行わない
- 非推奨・脆弱なアルゴリズム（MD5、SHA-1、DES、RC4 等）を使用しない

#### MASVS-CRYPTO-2: 安全な鍵管理

- 暗号鍵が必要になった場合は Android Keystore に保管し、アプリケーションコードやリソースにハードコードしない
- 鍵の生成には十分なエントロピーを持つ乱数生成器（`SecureRandom`）を使用する

---

## MASVS-AUTH: 認証・認可

> MASVS-AUTH covers authentication and authorization mechanisms, verifying user identity and enforcing proper access controls.
>
> --- [MASVS-AUTH](https://mas.owasp.org/MASVS/07-MASVS-AUTH/)

本アプリはリモートサーバーとの通信やユーザーアカウントの管理を行わないため、認証・認可の大部分は適用外となる。ただし、VPN サービスの起動にはユーザーの明示的な同意が求められる。

### 方針

#### MASVS-AUTH-1: 適切な認証メカニズムの使用

- VPN 接続の開始時には、Android OS が提供する VPN 接続確認ダイアログを通じてユーザーの明示的な同意を取得する
- 将来リモート機能を追加する場合は、サーバーサイドでの認証・認可を実装し、クライアント側のみの認証に依存しない

#### MASVS-AUTH-2: セッション管理

- 本アプリは現時点でセッション管理を必要としないが、将来ネットワーク通信を追加する場合はセキュアなトークンベースのセッション管理を導入する

---

## MASVS-NETWORK: ネットワーク通信

> MASVS-NETWORK covers secure network communication, ensuring data is encrypted in transit and network connections are properly validated.
>
> --- [MASVS-NETWORK](https://mas.owasp.org/MASVS/08-MASVS-NETWORK/)

本アプリは VPN サービスとしてデバイス上のネットワークトラフィックを処理するが、アプリ自体が外部サーバーへ通信を行うことはない。パケットフィルタリングはローカルの仮想ネットワークインターフェースを介して完結する。

### 方針

#### MASVS-NETWORK-1: 通信経路の暗号化

- アプリがネットワーク通信を行う場合は、TLS 1.2 以上を必須とする
- `network_security_config.xml` を明示的に定義し、平文通信（cleartext traffic）を禁止する
- 将来外部通信を追加する場合は、証明書の検証を適切に行い、自己署名証明書を受け入れない

#### MASVS-NETWORK-2: ネットワークセキュリティ設定

- Network Security Configuration を活用し、信頼する CA やドメインごとのポリシーを明示する
- デバッグビルドでのみユーザー追加 CA を信頼するよう制限し、リリースビルドではシステム CA のみを信頼する

#### VPN サービス固有の方針

- パケットフィルタリングで取得した情報を外部に送信しない。処理はすべてデバイスローカルで完結させる
- VPN トンネルの仮想 NIC 設定を必要最小限に保ち、不要なルーティングを行わない

---

## MASVS-PLATFORM: プラットフォーム連携

> MASVS-PLATFORM covers the secure use of platform-provided IPC mechanisms and WebViews, ensuring safe interaction with the operating system and other apps.
>
> --- [MASVS-PLATFORM](https://mas.owasp.org/MASVS/09-MASVS-PLATFORM/)

本アプリは VPN サービスやフォアグラウンドサービスなど、Android プラットフォームの特権的な機能を使用する。これらの機能は適切なパーミッション管理とコンポーネント保護のもとで運用する。

### 方針

#### MASVS-PLATFORM-1: プラットフォーム機能の安全な使用

- アプリが宣言するパーミッションは機能上必要最小限のものに限定する
- 新しいパーミッションを追加する際は、そのパーミッションが必要な理由をコードレビューで確認する
- センシティブなコンポーネント（Service、BroadcastReceiver 等）は `android:exported="false"` を設定し、外部アプリからのアクセスを防止する
- VPN サービスには `android:permission="android.permission.BIND_VPN_SERVICE"` を設定し、システム以外からのバインドを拒否する

#### MASVS-PLATFORM-2: WebView のセキュリティ

- WebView を使用する場合は JavaScript の実行を原則無効にし、必要な場合のみ最小限の範囲で有効にする
- WebView のコンテンツには信頼できるソースのみを読み込み、`file://` スキームによるローカルファイルアクセスを無効にする

#### MASVS-PLATFORM-3: IPC (プロセス間通信) の安全性

- 明示的 Intent のみを使用し、暗黙的 Intent による意図しないコンポーネント呼び出しを防止する
- Content Provider を公開する場合は適切なパーミッションを設定し、URI パーミッションの範囲を最小限にする
- ディープリンクを実装する場合は、受信データの検証を行い、不正なリンクからの操作を防止する

---

## MASVS-CODE: コード品質

> MASVS-CODE covers general coding best practices, including secure development, dependency management, and app update mechanisms.
>
> --- [MASVS-CODE](https://mas.owasp.org/MASVS/10-MASVS-CODE/)

コード品質の維持はセキュリティの基盤である。静的解析、自動テスト、依存関係の管理を通じて、脆弱性の混入を予防する。

### 方針

#### MASVS-CODE-1: アプリの署名と整合性の検証

- リリースビルドは適切な鍵で署名し、Google Play App Signing を利用する
- リリースビルドでは R8 によるコードの縮小・難読化を有効にする

#### MASVS-CODE-2: 依存関係のセキュリティ

- サードパーティライブラリは信頼できるソース（Maven Central、Google Maven）からのみ取得する
- Renovate Bot 等の自動ツールを活用し、依存関係を定期的に最新バージョンへ更新する
- 依存関係の更新時は変更内容（特にセキュリティ修正）を確認してからマージする
- ネイティブライブラリ（PcapPlusPlus）についても、セキュリティアップデートを追跡し適時更新する

#### MASVS-CODE-3: 入力の検証

- Room DAO の型付きクエリを使用し、SQL インジェクションを防止する
- データベースマイグレーションで `execSQL()` を使用する場合は、パラメータをバインド変数で渡し、文字列結合による SQL 組み立てを行わない
- JNI 経由でネイティブコードに渡すデータは、Java/Kotlin 側で事前に検証する

#### MASVS-CODE-4: セキュアなビルドとテスト

- CI パイプラインでフォーマットチェック（Spotless）、静的解析（Android Lint）、ユニットテストを自動実行する
- テストカバレッジを Kover で計測し、セキュリティに関わるコードパスのカバレッジを維持する
- デバッグ用の設定（`android:debuggable="true"` 等）がリリースビルドに含まれないことを CI で検証する

---

## MASVS-RESILIENCE: 耐タンパー性

> MASVS-RESILIENCE covers defense-in-depth measures such as obfuscation and anti-tampering mechanisms that protect the app against reverse engineering and specific client-side attacks.
>
> --- [MASVS-RESILIENCE](https://mas.owasp.org/MASVS/11-MASVS-RESILIENCE/)

VPN アプリはデバイスの全ネットワークトラフィックを扱うため、改ざんされた場合の影響が大きい。リバースエンジニアリングや改ざんに対する防御は、アプリの信頼性を維持するうえで重要である。

### 方針

#### MASVS-RESILIENCE-1: デバイスのセキュリティ状態の確認

- ルート化された端末での動作リスクを評価し、必要に応じてユーザーへの警告を実装する
- デバイスのロック画面が有効であることを推奨し、ユーザーに適切なガイダンスを提供する

#### MASVS-RESILIENCE-2: デバッグの防止

- リリースビルドでは `android:debuggable` を `false` に設定する
- リリース APK/AAB にデバッグシンボルや不要なデバッグ情報を含めない

#### MASVS-RESILIENCE-3: コードの難読化

- R8 によるコードの縮小・難読化をリリースビルドで有効にし、リバースエンジニアリングの難易度を高める
- ProGuard ルールで難読化から除外する範囲を必要最小限にする

#### MASVS-RESILIENCE-4: 改ざん検知

- Google Play Integrity API の導入を検討し、アプリの改ざんや不正な配布を検知する仕組みを整備する
- 改ざんが検知された場合の動作（警告表示、機能制限等）を定義する

---

## MASVS-PRIVACY: プライバシー

> MASVS-PRIVACY covers the protection of user data, ensuring it is handled in compliance with privacy regulations and that users are informed and in control.
>
> --- [MASVS-PRIVACY](https://mas.owasp.org/MASVS/12-MASVS-PRIVACY/)

本アプリはデバイスのネットワークトラフィック情報を取り扱うため、ユーザーのプライバシーに対する配慮が不可欠である。

### 方針

#### MASVS-PRIVACY-1: データの最小化

- パケットフィルタリングに必要な最小限の情報（パッケージ名、IP アドレス、ポート番号、プロトコル、タイムスタンプ）のみを収集する
- パケットのペイロード（通信内容）は保存しない
- 不要になったデータは速やかに削除する仕組みを設ける

#### MASVS-PRIVACY-2: ユーザーへの透明性

- アプリが収集するデータの種類と目的をユーザーに明示する
- Google Play のデータセーフティセクションに正確な情報を掲載する

#### MASVS-PRIVACY-3: トラッキングの制限

- 広告 ID やデバイス識別子を収集・送信しない
- サードパーティのアナリティクス SDK を使用する場合は、収集するデータの範囲を精査し、必要最小限に制限する

#### MASVS-PRIVACY-4: データの外部送信制限

- パケットフィルタリングで取得した情報を外部サービスに送信しない
- アプリの全データ処理をデバイスローカルで完結させる

---

## 参考資料

- [OWASP Mobile Application Security Verification Standard (MASVS) v2](https://mas.owasp.org/MASVS/)
- [OWASP Mobile Application Security Testing Guide (MASTG)](https://mas.owasp.org/MASTG/)
- [OWASP Mobile Application Security Weakness Enumeration (MASWE)](https://mas.owasp.org/MASWE/)
- [Android Security Best Practices](https://developer.android.com/topic/security/best-practices)
