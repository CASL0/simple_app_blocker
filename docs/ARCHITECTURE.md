# アーキテクチャガイド

本ドキュメントは、Simple App Blocker のアーキテクチャ方針を [Guide to app architecture](https://developer.android.com/topic/architecture) および [Architecture recommendations](https://developer.android.com/topic/architecture/recommendations) に基づいて定めるものである。

---

## 基本原則

Android 公式のアーキテクチャガイドでは、以下の原則を最重要としている。本プロジェクトでもこれらを設計の土台とする。

### 関心の分離 (Separation of Concerns)

> The primary role of an Activity or Fragment is to host your app's UI. The Android OS controls their lifecycle, frequently destroying and recreating them in response to user actions like screen rotation or system events like low memory. This ephemeral nature makes them unsuitable for holding application data or state.
>
> --- [Guide to app architecture](https://developer.android.com/topic/architecture)

Activity や Fragment にビジネスロジックやデータ操作を記述しない。これらはあくまで UI のホストであり、ライフサイクルに依存しないロジックは ViewModel やデータ層に分離する。

### 単一情報源 (Single Source of Truth)

> When a new data type is defined, assign a single source of truth (SSOT) to it. The SSOT is the owner of that data, and only the SSOT can modify it.
>
> --- [Guide to app architecture](https://developer.android.com/topic/architecture)

データ型ごとに単一の情報源を定める。本プロジェクトでは Room データベースがブロックログと許可リストの SSOT となる。データの変更は必ず SSOT を通じて行い、他のレイヤーが直接データを書き換えることを禁止する。

### 単方向データフロー (Unidirectional Data Flow)

> In Android, state or data usually flow from the higher-scoped types of the hierarchy to the lower-scoped ones. Events are usually triggered from the lower-scoped types until they reach the SSOT for the corresponding data type.
>
> --- [Guide to app architecture](https://developer.android.com/topic/architecture)

状態は上位（ViewModel）から下位（UI）へ一方向に流れ、イベントは逆方向に伝搬する。これにより状態の一貫性を保証し、デバッグを容易にする。

### データモデルによる UI の駆動 (Drive UI from Data Models)

データモデルは UI 要素やアプリコンポーネントから独立した存在とする。永続的なモデルを使用することで、ネットワーク接続が不安定な場合やプロセスが破棄された場合でもアプリが正常に動作する。

---

## レイヤードアーキテクチャ

> Each application should have at least two layers: the UI layer that displays application data on the screen, and the data layer that contains the business logic of your app and exposes application data.
>
> --- [Guide to app architecture](https://developer.android.com/topic/architecture)

本プロジェクトでは **UI 層**と**データ層**の 2 層を必須とし、必要に応じてドメイン層を導入する。

```
┌─────────────────────────────┐
│         UI Layer            │
│  (Activity / Compose / VM)  │
├─────────────────────────────┤
│       Domain Layer          │
│     (UseCase) ※任意         │
├─────────────────────────────┤
│        Data Layer           │
│  (Repository / DataSource)  │
└─────────────────────────────┘
```

### UI 層

> **Strongly recommended**: Use a clearly defined UI layer. The UI layer displays the application data on the screen and serves as the primary point of user interaction.
>
> --- [Architecture recommendations](https://developer.android.com/topic/architecture/recommendations)

UI 層は以下の 2 つの要素で構成する。

- **UI 要素**: Jetpack Compose の Composable 関数によりデータを画面に描画する
- **状態ホルダー**: ViewModel がデータを保持し、UI ステートとして公開する

#### ViewModel の方針

> **Strongly recommended**: ViewModels should be agnostic of the Android lifecycle. ViewModels shouldn't hold a reference to any Lifecycle-related type. Don't pass Activity, Fragment, Context, or Resources.
>
> --- [Architecture recommendations](https://developer.android.com/topic/architecture/recommendations)

- ViewModel は画面単位で作成し、再利用可能な UI コンポーネントには使用しない
- ViewModel は Android ライフサイクルに依存しない。Activity、Fragment、Context への参照を保持しない
- UI ステートは `StateFlow` で公開し、`stateIn` オペレーターの `WhileSubscribed(5_000)` ポリシーを使用する
- ViewModel から UI へイベントを送信しない。イベントは ViewModel 内で即座に処理し、状態更新として反映する

```kotlin
// 推奨パターン
@HiltViewModel
class ExampleViewModel @Inject constructor(
    repository: ExampleRepository
) : ViewModel() {

    val uiState: StateFlow<ExampleUiState> =
        repository.getDataStream()
            .map { ExampleUiState.Success(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ExampleUiState.Loading
            )
}
```

#### UI ステートの収集

> **Strongly recommended**: Collect UI state from the UI using the appropriate lifecycle-aware coroutine builder. Use `collectAsStateWithLifecycle` in Jetpack Compose.
>
> --- [Architecture recommendations](https://developer.android.com/topic/architecture/recommendations)

- Jetpack Compose では `collectAsStateWithLifecycle` を使用する
- View システムでは `repeatOnLifecycle(Lifecycle.State.STARTED)` を使用する

### データ層

> **Strongly recommended**: Use a clearly defined data layer. The data layer exposes application data to the rest of the app and contains the vast majority of business logic of your app.
>
> --- [Architecture recommendations](https://developer.android.com/topic/architecture/recommendations)

データ層は **リポジトリ**と**データソース**で構成する。

#### リポジトリの方針

> **Strongly recommended**: The data layer should expose application data using a repository. Components in the UI layer such as composables, activities, or ViewModels shouldn't interact directly with a data source.
>
> --- [Architecture recommendations](https://developer.android.com/topic/architecture/recommendations)

- 扱うデータの種類ごとにリポジトリクラスを作成する
- UI 層のコンポーネント（Composable、Activity、ViewModel）はデータソースに直接アクセスしない。必ずリポジトリを経由する
- リポジトリはデータの公開、変更の集約、複数データソース間の競合解決、データソースの抽象化を担う
- データソースが単一であっても、リポジトリを作成する

#### データソースの方針

- データソースは単一のデータ供給元（Room データベース、PackageManager 等）に対して 1 つ作成する
- データソースはアプリケーションとシステムの橋渡しであり、他のデータソースに依存しない

#### 命名規則

> Names for the implementations of interfaces should be meaningful. Have `Default` as the prefix if a better name cannot be found.
>
> --- [Architecture recommendations](https://developer.android.com/topic/architecture/recommendations)

- リポジトリのインターフェース名: `{データ型}Repository` (例: `AllowlistRepository`)
- リポジトリの実装名: `Default{データ型}Repository` (例: `DefaultAllowlistRepository`)
- テスト用のフェイク: `Fake{データ型}Repository` (例: `FakeAllowlistRepository`)
- データストリームのメソッド名: `get{Model}Stream()` (例: `getAllowlistStream()`)

### ドメイン層

> The domain layer is an optional layer between the UI and data layers, responsible for encapsulating complex business logic or simpler business logic reused by multiple ViewModels.
>
> --- [Guide to app architecture](https://developer.android.com/topic/architecture)

> **Recommended in big apps**: Use a domain layer and use cases if you need to reuse business logic that interacts with the data layer across multiple ViewModels, or simplify the business logic complexity of a particular ViewModel.
>
> --- [Architecture recommendations](https://developer.android.com/topic/architecture/recommendations)

ドメイン層は任意のレイヤーとする。以下のいずれかに該当する場合に UseCase クラスを導入する。

- 複数の ViewModel で同一のビジネスロジックを再利用する必要がある場合
- 特定の ViewModel のビジネスロジックが複雑化した場合

UseCase は単一の機能に対して責務を持ち、`operator fun invoke()` で呼び出し可能にする。

---

## モジュール構成

本プロジェクトは機能ごとのモジュール分割を行い、関心の分離をビルドシステムレベルで強制する。

### コアモジュール (`core/`)

機能横断的な共通コンポーネントを提供する。

| モジュール | 責務 |
| --- | --- |
| `core:model` | ドメインデータクラスの定義。純粋な Kotlin モジュールとし、Android フレームワークへの依存を最小限にする |
| `core:database` | Room データベース、DAO、エンティティ、マイグレーション |
| `core:data` | リポジトリインターフェースとデフォルト実装、データソース |
| `core:common` | DI 設定（Hilt アプリケーション、Dispatcher 等）、共通ユーティリティ |
| `core:ui` | 共有 Jetpack Compose コンポーネント、テーマ |
| `core:pcapplusplus` | JNI バインディングと PcapPlusPlus C++ ライブラリ |

### フィーチャーモジュール (`feature/`)

独立した縦断的な機能スライスを表す。各フィーチャーモジュールは以下の規約に従う。

- 他のフィーチャーモジュールに直接依存しない
- コアモジュール (`core:model`, `core:data`, `core:ui` 等) のみに依存する
- UI (Compose Screen)、ViewModel、フィーチャー固有のモデルを内包する

### 依存関係の方向

依存関係は常に上位レイヤーから下位レイヤーへの一方向とし、循環依存を禁止する。

```
app → feature:* → core:data → core:database → core:model
                 → core:ui
                 → core:model
                 → core:common
```

`app` モジュールは全フィーチャーモジュールを統合し、Jetpack Navigation によるナビゲーションを管理する。

---

## 依存性注入

> **Strongly recommended**: Use dependency injection best practices, mainly constructor injection when possible.
>
> --- [Architecture recommendations](https://developer.android.com/topic/architecture/recommendations)

> Use the dependency injection pattern and the Hilt library in Android apps. Hilt automatically constructs objects by walking the dependency tree, provides compile-time verification of dependencies, and creates dependency containers for Android framework classes.
>
> --- [Guide to app architecture](https://developer.android.com/topic/architecture)

- DI フレームワークとして Hilt を使用する
- コンストラクタインジェクションを原則とする
- `@Module` + `@InstallIn(SingletonComponent::class)` でモジュールを定義し、`@Provides` または `@Binds` でバインディングを提供する
- 共有が必要なミュータブルデータや初期化コストが高い型は `@Singleton` でスコープを設定する
- Coroutine Dispatcher はカスタム修飾子 (`@IoDispatcher` 等) を用いてインジェクトする

---

## 非同期処理

> **Strongly recommended**: Use coroutines and flows to communicate between layers.
>
> --- [Architecture recommendations](https://developer.android.com/topic/architecture/recommendations)

- レイヤー間のデータ通信には Kotlin Coroutines と Flow を使用する
- データ層は `Flow` でデータストリームを公開する
- ViewModel は `viewModelScope` 内で `suspend` 関数を呼び出す
- 長時間かかるブロッキング処理は適切なスレッドに移動する責務をその型自身が負う (Main-safe)

---

## モデルの方針

> **Recommended**: In complex apps, create new models in different layers or components when it makes sense.
>
> --- [Architecture recommendations](https://developer.android.com/topic/architecture/recommendations)

- レイヤー間のデータ変換にはレイヤーごとのモデルを定義する
- データベースエンティティ（Room `@Entity`）はデータ層に閉じ、UI 層には公開しない
- ドメインモデルは `core:model` に定義し、レイヤー間の共通言語とする
- エンティティからドメインモデルへの変換は拡張関数 (`asDomainModel()`) で行う
- UI 固有の表示情報（アイコン、フォーマット済みテキスト等）は ViewModel 内の UiState に含める

---

## テスト

> **Strongly recommended**: Unless the project is roughly as simple as a hello world app, you should test it, at minimum with: Unit test ViewModels, including Flows. Unit test data layer entities (repositories and data sources). UI navigation tests that are useful as regression tests in CI.
>
> --- [Architecture recommendations](https://developer.android.com/topic/architecture/recommendations)

> **Strongly recommended**: Prefer fakes to mocks.
>
> --- [Architecture recommendations](https://developer.android.com/topic/architecture/recommendations)

- ViewModel のユニットテストでは Flow の出力と状態遷移を検証する
- データ層のテストではリポジトリとデータソースを個別に検証する
- テストダブルにはモックよりフェイクを優先する
- 各レイヤーが独立してテスト可能な設計を維持する

---

## 参考資料

- [Guide to app architecture](https://developer.android.com/topic/architecture)
- [Architecture recommendations](https://developer.android.com/topic/architecture/recommendations)
- [UI layer](https://developer.android.com/topic/architecture/ui-layer)
- [Data layer](https://developer.android.com/topic/architecture/data-layer)
- [Domain layer](https://developer.android.com/topic/architecture/domain-layer)
