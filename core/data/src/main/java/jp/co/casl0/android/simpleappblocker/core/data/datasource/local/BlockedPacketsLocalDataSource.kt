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

package jp.co.casl0.android.simpleappblocker.core.data.datasource.local

import jp.co.casl0.android.simpleappblocker.core.data.datasource.BlockedPacketsDataSource
import jp.co.casl0.android.simpleappblocker.core.database.SimpleAppBlockerDatabase
import jp.co.casl0.android.simpleappblocker.core.database.model.BlockedPacket
import jp.co.casl0.android.simpleappblocker.core.database.model.asDomainModel
import jp.co.casl0.android.simpleappblocker.core.model.DomainBlockedPacket
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BlockedPacketsLocalDataSource @Inject constructor(private val database: SimpleAppBlockerDatabase) :
    BlockedPacketsDataSource {
    override fun getBlockedPacketsStream(): Flow<List<DomainBlockedPacket>> =
        database.blockedPacketsDao().getBlockedPackets().map {
            it.map { elem ->
                elem.asDomainModel()
            }
        }

    override suspend fun insertBlockedPacket(blockedPacket: DomainBlockedPacket) {
        database.blockedPacketsDao().insertBlockedPacket(
            BlockedPacket(
                packageName = blockedPacket.packageName.toString(),
                appName = blockedPacket.appName.toString(),
                srcAddress = blockedPacket.srcAddress.toString(),
                srcPort = blockedPacket.srcPort,
                dstAddress = blockedPacket.dstAddress.toString(),
                dstPort = blockedPacket.dstPort,
                protocol = blockedPacket.protocol.toString(),
                blockedAt = blockedPacket.blockedAt
            )
        )
    }
}
