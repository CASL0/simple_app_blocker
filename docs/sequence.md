# Simple App Blocker シーケンス図
Simple App Blocker のシーケンス図です。

## 許可アプリ更新
```plantuml
@startuml
autoactivate on
actor ユーザー as user
participant AppPackageListFragment
participant AppPackageListViewModel
participant AllowlistRepository
participant AppBlockerService
participant AllowlistDAO
database Room
user -> AppPackageListFragment : 許可アプリ更新
AppPackageListFragment -> AppPackageListViewModel : changeFiltersRule()
AppPackageListViewModel --> AllowlistRepository : insertAllowedPackage()
note right : Dispatchers.IOで実行
AllowlistRepository --> AllowlistDAO : insertAllowedPackages()
AllowlistDAO --> Room : 許可アプリをInsert
== Roomの更新 ==
AppPackageListViewModel <-- Room : Flowに更新値をemit
AppPackageListFragment <-- AppPackageListViewModel : LiveData.onChanged
AppPackageListFragment -> AppBlockerService : updateFilters()
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
BlockLogViewModel -> BlockLogViewModel : blockPacketInfoList.postValue()
return
BlockLogViewModel --> BlockLogFragment : LiveData.onChanged()
note right : ブロックしたアプリ一覧更新
@enduml
```
