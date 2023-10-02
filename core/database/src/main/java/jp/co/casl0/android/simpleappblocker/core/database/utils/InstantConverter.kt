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

package jp.co.casl0.android.simpleappblocker.core.database.utils

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

/** InstantとUnix時間のコンバーター */
class InstantConverter {

    /**
     * Instant → Unix時間の変換
     *
     * @param dateTime Instant
     * @return Unix時間
     */
    @TypeConverter
    fun instantToLong(dateTime: Instant): Long {
        return dateTime.toEpochMilliseconds()
    }

    /**
     * Unix時間 → Instantの変換
     *
     * @param epoch Unix時間
     * @return Instant
     */
    @TypeConverter
    fun longToInstant(epoch: Long): Instant {
        return Instant.fromEpochMilliseconds(epoch)
    }
}
