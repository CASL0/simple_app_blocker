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

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * ブロックログテーブルのブロック時刻のカラム定義変更のマイグレ
 * ISO文字列で保持していたので、エポック秒へ変換
 *
 * */
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
                CREATE TABLE new_blocked_packets (
                    package_name TEXT PRIMARY KEY NOT NULL,
                    app_name TEXT NOT NULL DEFAULT '',
                    src_address TEXT NOT NULL,
                    src_port INTEGER NOT NULL,
                    dst_address TEXT NOT NULL,
                    dst_port INTEGER NOT NULL,
                    protocol TEXT NOT NULL,
                    blocked_at INTEGER NOT NULL
                )
                ;
            """.trimIndent()
        )

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        database.query(
            "SELECT * FROM blocked_packets;"
        ).also { cursor ->
            while (cursor.moveToNext()) {
                val columnIndex = hashMapOf(
                    "package_name" to cursor.getColumnIndex("package_name"),
                    "app_name" to cursor.getColumnIndex("app_name"),
                    "src_address" to cursor.getColumnIndex("src_address"),
                    "src_port" to cursor.getColumnIndex("src_port"),
                    "dst_address" to cursor.getColumnIndex("dst_address"),
                    "dst_port" to cursor.getColumnIndex("dst_port"),
                    "protocol" to cursor.getColumnIndex("protocol"),
                    "blocked_at" to cursor.getColumnIndex("blocked_at")
                )
                val packageName =
                    columnIndex["package_name"]?.let { cursor.getString(it) } ?: continue
                val appName =
                    columnIndex["app_name"]?.let { cursor.getString(it) } ?: continue
                val srcAddress =
                    columnIndex["src_address"]?.let { cursor.getString(it) } ?: continue
                val srcPort =
                    columnIndex["src_port"]?.let { cursor.getInt(it) } ?: continue
                val dstAddress =
                    columnIndex["dst_address"]?.let { cursor.getString(it) } ?: continue
                val dstPort =
                    columnIndex["dst_port"]?.let { cursor.getInt(it) } ?: continue
                val protocol =
                    columnIndex["protocol"]?.let { cursor.getString(it) } ?: continue
                val blockedAt =
                    columnIndex["blocked_at"]?.let { cursor.getString(it) } ?: continue

                val dateTimeInstant =
                    LocalDateTime.parse(blockedAt, formatter).toKotlinLocalDateTime()
                        .toInstant(TimeZone.currentSystemDefault())
                database.execSQL(
                    """
                INSERT INTO new_blocked_packets 
                    (
                        package_name,
                        app_name,
                        src_address,
                        src_port,
                        dst_address,
                        dst_port,
                        protocol,
                        blocked_at
                    )
                VALUES
                    (
                        '$packageName',
                        '$appName',
                        '$srcAddress',
                        $srcPort,
                        '$dstAddress',
                        $dstPort,
                        '$protocol',
                        ${dateTimeInstant.toEpochMilliseconds()}
                    )
                ;
                """.trimIndent()
                )
            }
        }

        database.execSQL("DROP TABLE blocked_packets;")
        database.execSQL("ALTER TABLE new_blocked_packets RENAME TO blocked_packets;")
    }
}
