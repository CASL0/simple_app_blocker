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

import jp.co.casl0.android.simpleappblocker.core.data.datasource.InstalledApplicationDataSource
import jp.co.casl0.android.simpleappblocker.core.model.AppPackage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/** インストール済みアプリの操作用リポジトリ */
class DefaultInstalledApplicationRepository(
    private val installedApplicationDataSource: InstalledApplicationDataSource,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : InstalledApplicationRepository {

    /** インストール済みアプリ一覧のFlowを取得する関数 */
    override fun getInstalledApplicationsStream(): Flow<List<AppPackage>> {
        return installedApplicationDataSource.getInstalledApplicationsStream()
            .flowOn(defaultDispatcher)
    }

    /** インストール済みアプリ一覧を更新します */
    override suspend fun refresh() = withContext(defaultDispatcher) {
        installedApplicationDataSource.refreshInstalledApplications()
    }
}
