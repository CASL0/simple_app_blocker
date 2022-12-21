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
participant InstalledApplicationRepository
participant InstalledApplicationDataSource
participant AllowlistRepository
participant AllowlistDataSource
participant AllowlistDAO
database Room
participant MainActivity
participant AppBlockerService

user -> AllowlistFragment : [+]タップ
AllowlistFragment -> NewRuleDialog : show()

group NewRuleViewModel.init
    NewRuleViewModel -> NewRuleViewModel : refreshInstalledApplications()
    NewRuleViewModel --> InstalledApplicationRepository : refresh()
    InstalledApplicationRepository --> InstalledApplicationDataSource : refreshInstalledApplications()
    NewRuleViewModel --> InstalledApplicationRepository : collect installedApplications
    InstalledApplicationRepository -> InstalledApplicationDataSource :  getInstalledApplicationsStream()
    return
    NewRuleViewModel --> AllowlistRepository : collect allowlist
    AllowlistRepository -> AllowlistDataSource : getAllowlistStream()
    AllowlistDataSource -> AllowlistDAO : getAllowlist
    AllowlistDAO -> Room : SELECT * FROM allowlist
    return
    return
    return
end

group NewRuleDialog.onCreateView
    NewRuleDialog --> NewRuleViewModel : collect installedApplications
end
user -> NewRuleDialog : 許可したいアプリをタップ
NewRuleDialog -> NewRuleViewModel : createNewRule()
NewRuleViewModel --> AllowlistRepository : insertAllowedPackage()
AllowlistRepository --> AllowlistDataSource : insertPackage()
AllowlistDataSource --> AllowlistDAO : insertAllowedPackages()
AllowlistDAO --> Room : 許可アプリをInsert

== Roomの更新 ==

AllowlistRepository --> MainActivity : allowlistの更新をoffer
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
