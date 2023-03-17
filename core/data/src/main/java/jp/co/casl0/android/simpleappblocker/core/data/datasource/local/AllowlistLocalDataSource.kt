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

import jp.co.casl0.android.simpleappblocker.core.data.datasource.AllowlistDataSource
import jp.co.casl0.android.simpleappblocker.core.database.SimpleAppBlockerDatabase
import jp.co.casl0.android.simpleappblocker.core.database.model.AllowedPackage
import jp.co.casl0.android.simpleappblocker.core.database.model.asDomainModel
import jp.co.casl0.android.simpleappblocker.core.model.DomainAllowedPackage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class AllowlistLocalDataSource @Inject constructor(private val database: SimpleAppBlockerDatabase) :
    AllowlistDataSource {
    override fun getAllowlistStream(): Flow<List<DomainAllowedPackage>> =
        database.allowlistDao().getAllowlist().map {
            it.map { elem ->
                elem.asDomainModel()
            }
        }

    override suspend fun insertPackage(allowedPackage: DomainAllowedPackage) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        database.allowlistDao().insertAllowedPackages(
            AllowedPackage(
                packageName = allowedPackage.packageName.toString(),
                appName = allowedPackage.appName.toString(),
                addedTime = LocalDateTime.now().format(formatter)
            )
        )
    }

    override suspend fun removePackage(packageName: CharSequence) {
        database.allowlistDao().deleteByPackageName(packageName = packageName.toString())
    }
}
