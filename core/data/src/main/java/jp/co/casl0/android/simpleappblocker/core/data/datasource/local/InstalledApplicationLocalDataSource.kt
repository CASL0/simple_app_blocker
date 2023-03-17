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

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import jp.co.casl0.android.simpleappblocker.core.data.datasource.InstalledApplicationDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class InstalledApplicationLocalDataSource(@ApplicationContext private val context: Context) :
    InstalledApplicationDataSource {

    private val _installedApplications: MutableStateFlow<List<jp.co.casl0.android.simpleappblocker.core.model.AppPackage>> =
        MutableStateFlow(listOf())

    override suspend fun refreshInstalledApplications() {
        val pm = context.packageManager
        val installedApplications = if (Build.VERSION.SDK_INT >= 33) {
            pm.getInstalledApplications(PackageManager.ApplicationInfoFlags.of(0))
        } else {
            pm.getInstalledApplications(0)
        }.map {
            jp.co.casl0.android.simpleappblocker.core.model.AppPackage(
                it.loadIcon(pm),
                it.loadLabel(pm).toString(),
                it.packageName
            )
        }
        _installedApplications.update { installedApplications }
    }

    override fun getInstalledApplicationsStream(): Flow<List<jp.co.casl0.android.simpleappblocker.core.model.AppPackage>> =
        _installedApplications
}
