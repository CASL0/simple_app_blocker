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

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import jp.co.casl0.android.simpleappblocker.PacketInfo
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class BlockLogViewModel : ViewModel() {
    init {
        EventBus.getDefault().register(this)
    }

    companion object {
        private const val MAX_NUM_PACKET_LOG = 50
    }

    private val _blockPacketInfoList = mutableStateListOf<PacketInfo>()
    val blockPacketInfoList: List<PacketInfo>
        get() = _blockPacketInfoList

    /**
     * パケットブロック時のイベントハンドラ
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onPacketBlocked(packetInfo: PacketInfo) {
        if (blockPacketInfoList.size >= MAX_NUM_PACKET_LOG) {
            _blockPacketInfoList.removeLast()
        }
        _blockPacketInfoList.add(0, packetInfo)
    }
}