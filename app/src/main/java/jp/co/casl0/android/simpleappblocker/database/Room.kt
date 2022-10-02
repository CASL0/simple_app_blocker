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

package jp.co.casl0.android.simpleappblocker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AllowedPackage::class], version = 1, exportSchema = false)
abstract class AllowlistDatabase : RoomDatabase() {
    /**
     * 許可アプリリスト操作用のDAOを取得する関数
     */
    abstract fun allowlistDao(): AllowlistDAO

    companion object {
        /**
         * データベースのシングルトンインスタンス
         */
        private var INSTANCE: AllowlistDatabase? = null

        /**
         * データベースインスタンスを取得する関数
         */
        fun getDatabase(context: Context): AllowlistDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AllowlistDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
