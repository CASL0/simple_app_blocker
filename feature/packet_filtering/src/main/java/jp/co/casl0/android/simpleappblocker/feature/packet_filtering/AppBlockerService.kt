/*
 * Copyright 2022 CASL0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.casl0.android.simpleappblocker.feature.packet_filtering

import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
import android.net.ConnectivityManager
import android.net.VpnService
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.ServiceCompat
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import jp.co.casl0.android.simpleappblocker.core.data.repository.BlockedPacketsRepository
import jp.co.casl0.android.simpleappblocker.core.model.DomainBlockedPacket
import jp.co.casl0.android.simpleappblocker.core.pcapplusplus.model.ParsedPacket
import jp.co.casl0.android.simpleappblocker.feature.packet_filtering.utils.NOTIFICATION_ID
import jp.co.casl0.android.simpleappblocker.feature.packet_filtering.utils.VPN_SERVICE_SESSION
import jp.co.casl0.android.simpleappblocker.feature.packet_filtering.utils.createNotificationChannel
import jp.co.casl0.android.simpleappblocker.feature.packet_filtering.utils.getApplicationLabel
import jp.co.casl0.android.simpleappblocker.feature.packet_filtering.utils.getNotificationBuilder
import jp.co.casl0.android.simpleappblocker.feature.packet_filtering.utils.retrieveUid
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

@AndroidEntryPoint
class AppBlockerService : VpnService(), AppBlockerConnection.OnBlockPacketListener {
    inner class AppBlockerBinder : Binder() {
        fun getService(): AppBlockerService = this@AppBlockerService
    }

    @Inject
    lateinit var repository: BlockedPacketsRepository

    private val binder = AppBlockerBinder()
    private val connectingThread = AtomicReference<Thread>()

    override fun onCreate() {
        super.onCreate()
        updateForegroundService(getString(R.string.filters_disabled))
    }

    override fun onDestroy() {
        super.onDestroy()
        disableFilters()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    /**
     * フィルターを更新する関数
     *
     * @param allowedAppPackages 通信許可したいアプリパッケージ名のリスト
     */
    fun updateFilters(allowedAppPackages: List<CharSequence>) {
        updateForegroundService(getString(R.string.filters_enabled))
        startConnection(allowedAppPackages)
    }

    /** 遮断を停止する関数 */
    fun disableFilters() {
        setConnectingThread(null)
        updateForegroundService(getString(R.string.filters_disabled))
    }

    /** 遮断用のタスクを開始する関数 */
    private fun startConnection(allowedAppPackages: List<CharSequence>) {
        val localTunnelBuilder = getLocalTunnelBuilder()
        allowedAppPackages.forEach { allowedAppPackage ->
            try {
                // 遮断対象外のアプリは除外しシステムネットワークを使用するようにする
                Logger.d(allowedAppPackage)
                localTunnelBuilder?.addDisallowedApplication(allowedAppPackage.toString())
            } catch (e: PackageManager.NameNotFoundException) {
                Logger.d("Package not available")
            }
        }
        val tunnelInterface = localTunnelBuilder?.establish()
        if (tunnelInterface != null) {
            val thread = Thread(
                AppBlockerConnection(tunnelInterface).apply {
                    setOnBlockPacketListener(this@AppBlockerService)
                },
                "AppBlockerThread"
            )
            setConnectingThread(thread)
            thread.start()
        }
    }

    /**
     * 元の遮断用のスレッドを停止し、新規のスレッドを保持する関数
     *
     * @param thread 新規の遮断用スレッド
     */
    private fun setConnectingThread(thread: Thread?) {
        connectingThread.getAndSet(thread)?.interrupt()
    }

    /** 遮断用の仮想NICのBuilderを取得する関数 */
    private fun getLocalTunnelBuilder(): Builder? {
        return try {
            Builder()
                .setSession(VPN_SERVICE_SESSION)
                .addAddress("10.1.10.1", 32)
                .addAddress("fd00:1:fd00:1:fd00:1:fd00:1", 128)
                .addRoute("0.0.0.0", 0)
                .addRoute("::", 0)
        } catch (e: IllegalArgumentException) {
            e.localizedMessage?.let { Logger.d(it) }
            null
        }
    }

    /**
     * フォアグラウンドサービスを通知する関数
     *
     * @param message 通知に表示するメッセージ
     */
    private fun updateForegroundService(message: CharSequence) {
        Logger.d("updateForegroundService: $message")
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).run {
            createNotificationChannel(
                getString(R.string.notification_channel_id),
                getString(R.string.notification_channel_name)
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                getNotificationBuilder(message).build(),
                FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NOTIFICATION_ID, getNotificationBuilder(message).build())
        }
    }

    // AppBlockerConnection.OnBlockPacketListener
    @OptIn(DelicateCoroutinesApi::class)
    override fun onBlockPacket(blockedPacket: ParsedPacket) {
        val connectivityManager =
            applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val packageManager = applicationContext.packageManager
        val uid = connectivityManager.retrieveUid(blockedPacket)
        packageManager.getNameForUid(uid)?.let { packageName ->
            GlobalScope.launch {
                repository.insertBlockedPacket(
                    DomainBlockedPacket(
                        packageName = packageName,
                        appName = packageManager.getApplicationLabel(packageName),
                        srcAddress = blockedPacket.networkLayer.srcAddress,
                        srcPort = blockedPacket.transportLayer.srcPort,
                        dstAddress = blockedPacket.networkLayer.dstAddress,
                        dstPort = blockedPacket.transportLayer.dstPort,
                        protocol = blockedPacket.transportLayer.protocol,
                        blockedAt = Clock.System.now()
                    )
                )
            }
        }
    }
}
