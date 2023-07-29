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

package jp.co.casl0.android.simpleappblocker.core.data.datasource

import jp.co.casl0.android.simpleappblocker.core.model.DomainAllowedPackage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeAllowlistDataSource(allowedPackages: List<DomainAllowedPackage> = listOf()) :
    AllowlistDataSource {
    private val _allowedPackagesStream = MutableStateFlow(allowedPackages)

    override fun getAllowlistStream(): Flow<List<DomainAllowedPackage>> {
        return _allowedPackagesStream
    }

    override suspend fun insertPackage(allowedPackage: DomainAllowedPackage) {
        val currentList = _allowedPackagesStream.value.toMutableList()
        val index =
            currentList.binarySearchBy(allowedPackage.packageName.toString()) { it.packageName.toString() }
        if (index != -1) {
            currentList[index] = allowedPackage
        } else {
            currentList.add(allowedPackage)
        }
        _allowedPackagesStream.update { currentList }
    }

    override suspend fun removePackage(packageName: CharSequence) {
        val currentList = _allowedPackagesStream.value.toMutableList()
        val index =
            currentList.binarySearchBy(packageName.toString()) { it.packageName.toString() }
        if (index != -1) {
            currentList.removeAt(index)
        }
        _allowedPackagesStream.update { currentList }
    }
}
