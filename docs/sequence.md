# Simple App Blocker シーケンス図
Simple App Blocker のシーケンス図です。

## 許可アプリ更新
```plantuml
@startuml
autoactivate on
actor ユーザー as user
participant ApplistFragment
participant ApplistViewModel
participant AppBlockerService
participant AppBlockerTask
participant AllowlistDAO
user -> ApplistFragment : 許可アプリ更新
ApplistFragment -> ApplistViewModel : updateAllowlist()
ApplistViewModel -> AppBlockerService : connectTun()
note right : 仮想インターフェースを更新
AppBlockerService --> AppBlockerTask : run()
ApplistViewModel --> AllowlistDAO : insertPackages()
note right : AppDatabaseに許可アプリ一覧を保存
@enduml
```

## アプリブロック
```plantuml
@startuml
autoactivate on
participant AppBlockerService
participant BlocklistViewModel
participant BlocklistFragment
participant EventBus
AppBlockerService -> AppBlockerService : analyzePacket()
return
AppBlockerService --> EventBus : ブロックしたパケット情報をpost
== EventBusから通知受信 ==
EventBus --> BlocklistViewModel : onPacketBlocked()
BlocklistViewModel -> BlocklistViewModel : blocklist.postValue()
return
BlocklistViewModel --> BlocklistFragment : LiveData.onChanged()
note right : ブロックしたアプリ一覧更新
@enduml
```
