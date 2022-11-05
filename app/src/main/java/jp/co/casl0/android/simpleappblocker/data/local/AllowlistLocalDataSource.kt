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

package jp.co.casl0.android.simpleappblocker.data.local

import jp.co.casl0.android.simpleappblocker.data.AllowlistDataSource
import jp.co.casl0.android.simpleappblocker.database.AllowedPackage
import jp.co.casl0.android.simpleappblocker.database.AllowlistDatabase
import jp.co.casl0.android.simpleappblocker.database.asDomainModel
import jp.co.casl0.android.simpleappblocker.model.DomainAllowedPackage
import jp.co.casl0.android.simpleappblocker.utilities.getNowDateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AllowlistLocalDataSource @Inject constructor(private val database: AllowlistDatabase) :
    AllowlistDataSource {
    override fun getAllowlistStream(): Flow<List<DomainAllowedPackage>> =
        database.allowlistDao().getAllowlist().map { it.asDomainModel() }

    override suspend fun insertPackage(allowedPackage: DomainAllowedPackage) {
        database.allowlistDao().insertAllowedPackages(
            AllowedPackage(
                packageName = allowedPackage.packageName.toString(),
                appName = allowedPackage.appName.toString(),
                addedTime = getNowDateTime()
            )
        )
    }

    override suspend fun removePackage(packageName: CharSequence) {
        database.allowlistDao().deleteByPackageName(packageName = packageName.toString())
    }
}
