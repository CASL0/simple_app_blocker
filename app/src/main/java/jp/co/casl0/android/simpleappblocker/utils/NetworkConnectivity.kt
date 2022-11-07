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

package jp.co.casl0.android.simpleappblocker.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.TrafficStats
import android.os.Build
import android.os.Process.INVALID_UID
import android.system.OsConstants
import com.orhanobut.logger.Logger
import jp.co.casl0.android.simpleappblocker.model.PacketInfo
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.UnknownHostException

open class NetworkConnectivity(context: Context?) {
    private val connectivityManager =
        context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

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

    /**
     * パケット情報からuidを取得する関数
     * @param packetInfo パケット情報
     * @return 取得したuid、取得に失敗した場合は -1
     */
    open fun retrieveUid(packetInfo: PacketInfo): Int {
        if (Build.VERSION.SDK_INT >= 29) {
            val protocol = when (packetInfo.protocol) {
                "TCP" -> OsConstants.IPPROTO_TCP
                "UDP" -> OsConstants.IPPROTO_UDP
                else -> -1
            }
            val localSocketAddress =
                translateInetSocketAddress(packetInfo.srcAddress, packetInfo.srcPort)
            val remoteSocketAddress =
                translateInetSocketAddress(packetInfo.dstAddress, packetInfo.dstPort)
            if (localSocketAddress != null && remoteSocketAddress != null) {
                val uid = connectivityManager.getConnectionOwnerUid(
                    protocol,
                    localSocketAddress,
                    remoteSocketAddress
                )
                if (uid != INVALID_UID) {
                    return uid
                }
            }
        }
        return -1
    }

    /**
     * IPアドレスの情報からInetSocketAddressに変換する関数
     * @param address IPアドレスの文字列
     * @param port ポート
     * @return 変換後のInetSocketAddressのインスタンス、変換できなかった場合はnull
     */
    private fun translateInetSocketAddress(address: String, port: Int): InetSocketAddress? =
        try {
            InetSocketAddress(InetAddress.getByName(address), port)
        } catch (e: UnknownHostException) {
            val errMsg = e.localizedMessage
            if (errMsg != null) Logger.d(errMsg)
            null
        } catch (e: IllegalArgumentException) {
            val errMsg = e.localizedMessage
            if (errMsg != null) Logger.d(errMsg)
            null
        }
}
