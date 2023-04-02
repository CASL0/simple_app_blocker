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

import android.net.ConnectivityManager
import android.os.Process
import android.system.OsConstants
import androidx.annotation.RequiresApi
import com.orhanobut.logger.Logger
import jp.co.casl0.android.simpleappblocker.core.pcapplusplus.model.ParsedPacket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.UnknownHostException

/**
 * パケット情報からuidを取得する関数
 *
 * @param blockedPacket パケット情報
 * @return 取得したuid、取得に失敗した場合は -1
 */
@RequiresApi(api = 29)
internal fun ConnectivityManager.retrieveUid(blockedPacket: ParsedPacket): Int {
    val protocol = when (blockedPacket.transportLayer.protocol) {
        "TCP" -> OsConstants.IPPROTO_TCP
        "UDP" -> OsConstants.IPPROTO_UDP
        else -> -1
    }
    val localSocketAddress =
        translateInetSocketAddress(
            blockedPacket.networkLayer.srcAddress,
            blockedPacket.transportLayer.srcPort
        )
    val remoteSocketAddress =
        translateInetSocketAddress(
            blockedPacket.networkLayer.dstAddress,
            blockedPacket.transportLayer.dstPort
        )
    if (localSocketAddress != null && remoteSocketAddress != null) {
        try {
            val uid = getConnectionOwnerUid(
                protocol,
                localSocketAddress,
                remoteSocketAddress
            )
            if (uid != Process.INVALID_UID) {
                return uid
            }
        } catch (e: SecurityException) {
            e.localizedMessage?.let { Logger.d(it) }
        } catch (e: IllegalArgumentException) {
            e.localizedMessage?.let { Logger.d(it) }
        }
    }
    return -1
}

/**
 * IPアドレスの情報からInetSocketAddressに変換する関数
 *
 * @param address IPアドレスの文字列
 * @param port ポート
 * @return 変換後のInetSocketAddressのインスタンス、変換できなかった場合はnull
 */
private fun translateInetSocketAddress(address: String, port: Int): InetSocketAddress? =
    try {
        InetSocketAddress(InetAddress.getByName(address), port)
    } catch (e: UnknownHostException) {
        e.localizedMessage?.let { Logger.d(it) }
        null
    } catch (e: IllegalArgumentException) {
        e.localizedMessage?.let { Logger.d(it) }
        null
    }
