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

package jp.co.casl0.android.simpleappblocker.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import jp.co.casl0.android.simpleappblocker.core.database.dao.AllowlistDAO
import jp.co.casl0.android.simpleappblocker.core.database.dao.BlockedPacketsDAO
import jp.co.casl0.android.simpleappblocker.core.database.model.AllowedPackage
import jp.co.casl0.android.simpleappblocker.core.database.model.BlockedPacket

@Database(
    entities = [AllowedPackage::class, BlockedPacket::class],
    version = 1,
    exportSchema = true
)
abstract class SimpleAppBlockerDatabase : RoomDatabase() {
    /** 許可アプリリスト操作用のDAOを取得する関数 */
    abstract fun allowlistDao(): AllowlistDAO

    /** ブロックしたパケット操作用のDAOを取得する関数 */
    abstract fun blockedPacketsDao(): BlockedPacketsDAO
}
