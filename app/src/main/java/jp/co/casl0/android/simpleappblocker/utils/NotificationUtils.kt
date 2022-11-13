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

package jp.co.casl0.android.simpleappblocker.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import jp.co.casl0.android.simpleappblocker.R
import jp.co.casl0.android.simpleappblocker.ui.MainActivity

internal const val NOTIFICATION_ID = 100

/**
 * 通知のインスタンスビルダー
 */
internal fun Context.getNotificationBuilder(messageBody: CharSequence) =
    NotificationCompat.Builder(this, this.getString(R.string.notification_channel_id))
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentText(messageBody)
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
    return if (Build.VERSION.SDK_INT >= 23) {
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    } else {
        PendingIntent.FLAG_UPDATE_CURRENT
    }
}

/**
 * 通知チャネルを作成します
 */
internal fun NotificationManager.createNotificationChannel(
    channelId: CharSequence,
    channelName: CharSequence
) {
    if (Build.VERSION.SDK_INT >= 26) {
        val notificationChannel = NotificationChannel(
            channelId.toString(),
            channelName,
            NotificationManager.IMPORTANCE_LOW
        )
        createNotificationChannel(notificationChannel)
    }
}
