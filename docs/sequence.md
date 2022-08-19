# Simple App Blocker シーケンス図

Simple App Blocker のシーケンス図です。

## 許可アプリ更新

```plantuml
@startuml
autoactivate on
actor ユーザー as user
participant AllowlistFragment
participant NewRuleDialog
participant NewRuleViewModel
participant AllowlistRepository
participant AllowlistDAO
database Room
participant MainActivity
participant AppBlockerService

user -> AllowlistFragment : [+]クリック
AllowlistFragment -> NewRuleDialog : show()
NewRuleDialog -> NewRuleViewModel : loadInstalledPackages()
return
return
user -> NewRuleDialog : 許可したいアプリをタップ
NewRuleDialog -> NewRuleViewModel : createNewRule()
NewRuleViewModel --> AllowlistRepository : insertAllowedPackage()
note right : Dispatchers.IOで実行
AllowlistRepository --> AllowlistDAO : insertAllowedPackages()
AllowlistDAO --> Room : 許可アプリをInsert

== Roomの更新 ==

Room --> MainActivity : Flowの更新をcollect
MainActivity -> AppBlockerService : updateFilters()
note right : 更新後の規則を適用
return
@enduml
```

## アプリブロック

```plantuml
@startuml
autoactivate on
actor ユーザー as user
participant AppBlockerService
participant AppBlockerConnection
participant BlockLogViewModel
participant BlockLogFragment
participant EventBus
user -> AppBlockerService : フィルター適用
AppBlockerService --> AppBlockerConnection : run()
== アプリの通信をブロック ==
AppBlockerConnection -> AppBlockerConnection : パケットの解析
return
AppBlockerConnection --> EventBus : ブロックしたパケット情報をpost
== EventBusのブロードキャスト ==
EventBus --> BlockLogViewModel : EventBusから通知
BlockLogViewModel -> BlockLogViewModel : onPacketBlocked()
BlockLogViewModel -> BlockLogViewModel : blockPacketInfoListの更新
return
BlockLogViewModel --> BlockLogFragment : Stateの更新
BlockLogFragment -> BlockLogFragment : recompose
note right : ブロックしたアプリ一覧更新
return
@enduml
```
