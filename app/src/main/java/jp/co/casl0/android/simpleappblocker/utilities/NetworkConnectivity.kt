/*
 * Copyright 2022 CASL0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package jp.co.casl0.android.simpleappblocker.utilities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.TrafficStats

class NetworkConnectivity {
    companion object {
        /**
         * アップロード中またはダウンロード中であるかを判定する関数
         * @param uid ネットワーク接続を確認したいアプリのUID
         * @return アップロード中またはダウンロード中はtrue、それ以外はfalse
         */
        fun isConnectingNetwork(uid: Int): Boolean {
            val downloaded = TrafficStats.getUidRxBytes(uid)
            val uploaded = TrafficStats.getUidTxBytes(uid)
            return downloaded > 0 || uploaded > 0
        }

        /**
         * Manifest.permission.INTERNETが付与されているか判定されている関数
         * @param packageName 付与されているか確認したいパッケージ名
         * @return Manifest.permission.INTERNETが付与されていればtrue、それ以外はfalse
         */
        fun hasInternetPermission(context: Context?, packageName: String): Boolean =
            context?.packageManager?.checkPermission(
                Manifest.permission.INTERNET, packageName
            ) == PackageManager.PERMISSION_GRANTED
    }
}