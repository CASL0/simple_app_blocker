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

package jp.co.casl0.android.simpleappblocker.repository

import jp.co.casl0.android.simpleappblocker.data.AllowlistDataSource
import jp.co.casl0.android.simpleappblocker.model.DomainAllowedPackage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AllowlistRepository @Inject constructor(
    private val allowlistDataSource: AllowlistDataSource,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    val allowlist: Flow<List<CharSequence>> =
        allowlistDataSource.getAllowlistStream().map { allowedPackages ->
            allowedPackages.map {
                it.packageName
            }
        }.flowOn(defaultDispatcher)

    /**
     * Roomに許可アプリを追加する関数
     * @param packageName パッケージ名
     * @param appName アプリ名
     */
    suspend fun insertAllowedPackage(packageName: String, appName: String) =
        withContext(defaultDispatcher) {
            allowlistDataSource.insertPackage(
                DomainAllowedPackage(
                    packageName, appName
                )
            )
        }

    /**
     * Roomから許可アプリのレコードを削除する関数
     */
    suspend fun disallowPackage(packageName: String) = withContext(defaultDispatcher) {
        allowlistDataSource.removePackage(packageName)
    }
}
