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

package jp.co.casl0.android.simpleappblocker.feature.packet_filtering.utils

import android.content.pm.PackageManager
import android.os.Build
import com.orhanobut.logger.Logger

/**
 * パッケージ名からアプリ名を引いてきます
 *
 * @param packageName パッケージ名
 * @return 見つかったアプリ名、見つからなかった場合はパッケージ名を返します
 */
internal fun PackageManager.getApplicationLabel(packageName: CharSequence): CharSequence {
    return try {
        val appInfo = if (Build.VERSION.SDK_INT >= 33) {
            getApplicationInfo(
                packageName.toString(),
                PackageManager.ApplicationInfoFlags.of(0)
            )
        } else {
            getApplicationInfo(
                packageName.toString(),
                0
            )
        }
        appInfo.loadLabel(this)
    } catch (e: PackageManager.NameNotFoundException) {
        e.localizedMessage?.let { Logger.d(it) }
        packageName
    }
}
