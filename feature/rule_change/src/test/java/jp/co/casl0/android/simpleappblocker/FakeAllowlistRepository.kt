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

import jp.co.casl0.android.simpleappblocker.core.data.repository.AllowlistRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

/** 許可アプリ一覧のデータ層のFake */
class FakeAllowlistRepository(private val dispatcher: CoroutineDispatcher) : AllowlistRepository {
    private val _allowlist = MutableStateFlow(listOf<CharSequence>())

    override fun getAllowlistStream(): Flow<List<CharSequence>> = _allowlist

    override suspend fun insertAllowedPackage(packageName: String, appName: String) =
        withContext(dispatcher) {
            val current = _allowlist.value.toMutableList()
            if (!current.contains(packageName)) {
                current.add(packageName)
                _allowlist.value = current
            }
        }

    override suspend fun disallowPackage(packageName: String) = withContext(dispatcher) {
        val current = _allowlist.value.toMutableList()
        if (current.contains(packageName)) {
            current.remove(packageName)
            _allowlist.value = current
        }
    }
}
