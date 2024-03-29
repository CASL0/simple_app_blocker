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

package jp.co.casl0.android.simpleappblocker.core.data.repository

import jp.co.casl0.android.simpleappblocker.core.model.DomainBlockedPacket
import kotlinx.coroutines.flow.Flow

/** ブロックログ機能のデータ層のインターフェース */
interface BlockedPacketsRepository {
    /** ブロックログのFlowを取得する関数 */
    fun getBlockedPacketsStream(): Flow<List<DomainBlockedPacket>>

    /**
     * ブロックログを追加する関数
     *
     * @param domainBlockedPacket 追加するブロックログ
     */
    suspend fun insertBlockedPacket(domainBlockedPacket: DomainBlockedPacket)
}
