/*
 * Copyright 2022 CASL0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.casl0.android.simpleappblocker.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.net.VpnService
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.orhanobut.logger.Logger
import jp.co.casl0.android.simpleappblocker.R
import jp.co.casl0.android.simpleappblocker.ui.MainActivity
import java.util.concurrent.atomic.AtomicReference


class AppBlockerService : VpnService() {
    inner class AppBlockerBinder : Binder() {
        fun getService(): AppBlockerService = this@AppBlockerService
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "jp.co.casl0.android.simpleappblocker.channelid"
        private const val NOTIFICATION_ID = 100
    }

    private val binder = AppBlockerBinder()
    private val connectingThread = AtomicReference<Thread>()
    var enabled = false

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
     * @param allowedAppPackages 通信許可したいアプリパッケージ名のリスト
     */
    fun updateFilters(allowedAppPackages: List<CharSequence>) {
        updateForegroundService(getString(R.string.filters_enabled))
        startConnection(allowedAppPackages)
        enabled = true
    }

    /**
     * 遮断を停止する関数
     */
    fun disableFilters() {
        setConnectingThread(null)
        updateForegroundService(getString(R.string.filters_disabled))
        enabled = false
    }

    /**
     * 遮断用のタスクを開始する関数
     */
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
            val thread = Thread(AppBlockerConnection(tunnelInterface), "AppBlockerThread")
            setConnectingThread(thread)
            thread.start()
        }
    }

    /**
     * 元の遮断用のスレッドを停止し、新規のスレッドを保持する関数
     * @param thread 新規の遮断用スレッド
     */
    private fun setConnectingThread(thread: Thread?) {
        connectingThread.getAndSet(thread)?.interrupt()
    }

    /**
     * 遮断用の仮想NICのBuilderを取得する関数
     */
    private fun getLocalTunnelBuilder(): Builder? {
        return try {
            Builder()
                .setSession(getString(R.string.app_name))
                .addAddress("10.1.10.1", 32)
                .addAddress("fd00:1:fd00:1:fd00:1:fd00:1", 128)
                .addRoute("0.0.0.0", 0)
                .addRoute("::", 0)
        } catch (e: IllegalArgumentException) {
            val errMsg = e.localizedMessage
            if (errMsg != null) Logger.d(errMsg)
            null
        }
    }

    /**
     * フォアグラウンドサービスを通知する関数
     * @param message 通知に表示するメッセージ
     */
    private fun updateForegroundService(message: String) {
        Logger.d("updateForegroundService: $message")
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, getNotificationBuilder(message).build())
    }

    /**
     * 通知チャネルを作成する関数(APIレベル26以上)
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT > 26) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_LOW
            )
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).also {
                it.createNotificationChannel(notificationChannel)
            }
        }
    }

    /**
     * フォアグラウンドサービスの通知Builderを取得する関数
     */
    private fun getNotificationBuilder(message: String): NotificationCompat.Builder =
        NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    NOTIFICATION_ID,
                    Intent(this, MainActivity::class.java),
                    getPendingIntentFlag()
                )
            )

    /**
     * 通知に指定するPendingIntentのフラグを取得する関数
     */
    private fun getPendingIntentFlag(): Int {
        var pendingIntentFlag = PendingIntent.FLAG_UPDATE_CURRENT
        if (Build.VERSION.SDK_INT >= 23) {
            pendingIntentFlag = pendingIntentFlag or PendingIntent.FLAG_IMMUTABLE
        }
        return pendingIntentFlag
    }
}
