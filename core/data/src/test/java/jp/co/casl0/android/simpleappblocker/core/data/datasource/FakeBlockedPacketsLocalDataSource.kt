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

package jp.co.casl0.android.simpleappblocker.core.data.datasource

import jp.co.casl0.android.simpleappblocker.core.model.DomainBlockedPacket
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeBlockedPacketsLocalDataSource(blockedPackets: List<DomainBlockedPacket>) :
    BlockedPacketsDataSource {
    private val _blockedPacketsStream = MutableStateFlow(blockedPackets)

    override fun getBlockedPacketsStream(): Flow<List<DomainBlockedPacket>> {
        return _blockedPacketsStream
    }

    override suspend fun insertBlockedPacket(blockedPacket: DomainBlockedPacket) {
        val currentList = _blockedPacketsStream.value.toMutableList()
        val index =
            currentList.binarySearchBy(blockedPacket.packageName.toString()) { it.packageName.toString() }
        if (index != -1) {
            currentList[index] = blockedPacket
        } else {
            currentList.add(blockedPacket)
        }
        _blockedPacketsStream.update { currentList }
    }
}
