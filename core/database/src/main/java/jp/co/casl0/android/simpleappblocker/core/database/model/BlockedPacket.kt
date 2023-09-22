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

package jp.co.casl0.android.simpleappblocker.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import jp.co.casl0.android.simpleappblocker.core.model.DomainBlockedPacket

@Entity(tableName = "blocked_packets")
data class BlockedPacket(
    @PrimaryKey
    @ColumnInfo(name = "package_name")
    val packageName: String,

    @ColumnInfo(name = "app_name", defaultValue = "")
    val appName: String,

    @ColumnInfo(name = "src_address")
    val srcAddress: String,

    @ColumnInfo(name = "src_port")
    val srcPort: Int,

    @ColumnInfo(name = "dst_address")
    val dstAddress: String,

    @ColumnInfo(name = "dst_port")
    val dstPort: Int,

    @ColumnInfo(name = "protocol")
    val protocol: String,

    @ColumnInfo(name = "blocked_at")
    val blockedAt: String
)

fun BlockedPacket.asDomainModel(): DomainBlockedPacket {
    return DomainBlockedPacket(
        packageName = this.packageName,
        appName = this.appName,
        srcAddress = this.srcAddress,
        srcPort = this.srcPort,
        dstAddress = this.dstAddress,
        dstPort = this.dstPort,
        protocol = this.protocol,
        blockedAt = this.blockedAt
    )
}
