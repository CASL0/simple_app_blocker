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

package jp.co.casl0.android.simpleappblocker.blocklog

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.orhanobut.logger.Logger
import jp.co.casl0.android.simpleappblocker.model.AppPackage
import jp.co.casl0.android.simpleappblocker.model.PacketInfo
import jp.co.casl0.android.simpleappblocker.utilities.NetworkConnectivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class BlockLogViewModel(context: Context?) : ViewModel() {
    private val packageManager = context?.packageManager
    private val networkConnectivity = NetworkConnectivity(context)

    init {
        EventBus.getDefault().register(this)
    }

    companion object {
        private const val MAX_NUM_PACKET_LOG = 200
    }

    private val _blockPacketInfoList = mutableStateListOf<Pair<PacketInfo, AppPackage?>>()
    val blockPacketInfoList: List<Pair<PacketInfo, AppPackage?>>
        get() = _blockPacketInfoList

    /**
     * パケットブロック時のイベントハンドラ
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onPacketBlocked(packetInfo: PacketInfo) {
        if (blockPacketInfoList.size >= MAX_NUM_PACKET_LOG) {
            _blockPacketInfoList.removeFirst()
        }
        val uid = networkConnectivity.retrieveUid(packetInfo)
        val packageName = lookupAppPackage(uid)
        if (packageName != null) {
            _blockPacketInfoList.add(Pair(packetInfo, packageName))
        }
    }

    /**
     * uidからアプリ情報を取得する関数
     * @param uid アプリ情報を取得したいパッケージのuid
     * @return 取得したアプリ情報、見つからなかった場合はnull
     */
    private fun lookupAppPackage(uid: Int): AppPackage? {
        packageManager?.getNameForUid(uid)?.also { packageName ->
            val appPackage = try {
                val appInfo = if (Build.VERSION.SDK_INT >= 33) {
                    packageManager.getPackageInfo(
                        packageName,
                        PackageManager.PackageInfoFlags.of(0)
                    ).applicationInfo
                } else {
                    packageManager.getPackageInfo(packageName, 0).applicationInfo
                }
                AppPackage(
                    appInfo.loadIcon(packageManager),
                    appInfo.loadLabel(packageManager).toString(),
                    appInfo.packageName,
                )
            } catch (e: PackageManager.NameNotFoundException) {
                e.localizedMessage?.let {
                    Logger.d(it)
                }
                null
            }
            return appPackage
        }
        return null
    }
}

class BlockLogViewModelFactory(private val context: Context?) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BlockLogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BlockLogViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
