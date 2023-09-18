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

package jp.co.casl0.android.simpleappblocker

import jp.co.casl0.android.simpleappblocker.core.data.repository.InstalledApplicationRepository
import jp.co.casl0.android.simpleappblocker.core.model.AppPackage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

/** インストール済みアプリ一覧データ層のリポジトリのスパイ */
class SpyInstalledApplicationRepository(
    fakeInstalledApps: List<AppPackage> = listOf(),
    private val dispatcher: CoroutineDispatcher
) :
    InstalledApplicationRepository {
    private val _installedApps = MutableStateFlow(fakeInstalledApps)

    /** refreshメソッド呼び出し回数を記録 */
    private var _refreshCallCount = 0
    val refreshCallCount get() = _refreshCallCount


    override fun getInstalledApplicationsStream(): Flow<List<AppPackage>> =
        _installedApps

    /** 遅延をエミュレートするためにdelayする */
    override suspend fun refresh() {
        withContext(dispatcher) {
            delay(100)
            _refreshCallCount++
        }
    }
}
