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

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AllowlistDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllowedPackages(vararg packages: AllowedPackage)

    @Delete
    suspend fun deleteAllowedPackages(vararg packages: AllowedPackage)

    @Query("SELECT package_name FROM allowlist")
    fun getAllowedPackages(): Flow<List<String>>

    @Query("SELECT * FROM allowlist")
    fun getAllowlist(): Flow<List<AllowedPackage>>

    @Query("DELETE FROM allowlist WHERE package_name=:packageName")
    suspend fun deleteByPackageName(packageName: String)
}
